# The Migration Blueprint 🗺️

## Zero-Downtime Implementation Roadmap

> **Note:** This document covers migration for the **Simplified Batch-Only Architecture** (no events, no queues, just a batch job).

This document provides step-by-step instructions for implementing the aggregation strategy without service interruption.

---

## Pre-Migration Checklist

### Database Backup

```bash
# Full backup before migration
pg_dump -h localhost -U postgres goodsprice > backup_pre_migration.sql

# Verify backup
grep "CREATE TABLE" backup_pre_migration.sql | wc -l
```

### Health Checks

```bash
# Verify current system health
curl http://localhost:8080/actuator/health

# Check database connections
mvn flyway:info -Pflyway
```

### Feature Flags

```yaml
# application-migration.yml
features:
  price-summary:
    enabled: false  # Will enable gradually
    batch:
      enabled: false
      interval: 15m  # 15 minutes (adjust as needed: 5m, 15m, 30m)
      batch-size: 100
    notification:
      enabled: false
```

---

## Phase 1: Foundation (Day 1-2)

### Step 1.1: Create Summary Table

```sql
-- V6__create_product_price_summaries_table.sql
-- (See THE_GRAND_AGGREGATION_STRATEGY.md for full SQL)

-- Run migration
mvn flyway:migrate -Pflyway
```

### Step 1.2: Deploy Code (No Logic Yet)

```java
// Deploy with feature flag OFF
@Service
public class PriceSummaryService {
    
    @Value("${features.price-summary.enabled:false}")
    private boolean enabled;
    
    public void updateSummary(Long productId) {
        if (!enabled) {
            return; // NO-OP for now
        }
        // ... actual logic
    }
}
```

**Verification:**
```bash
# Check table created
psql -U postgres -d goodsprice -c "\dt"

# Should see: product_price_summaries
```

---

## Phase 2: Backfill (Day 2-3)

### Step 2.1: Initial Population Job

```java
@Component
@RequiredArgsConstructor
public class BackfillJob {
    
    private final ProductRepository productRepository;
    private final PriceSummaryCalculator calculator;
    
    /**
     * Run once on startup (manually triggered)
     */
    @EventListener(ApplicationReadyEvent.class)
    @ConditionalOnProperty(name = "jobs.backfill.enabled", havingValue = "true")
    public void runBackfill() {
        log.info("Starting backfill job");
        
        // Get all product IDs
        List<Long> allProductIds = productRepository.findAllIds();
        
        // Process in batches
        List<List<Long>> batches = Lists.partition(allProductIds, 100);
        
        for (int i = 0; i < batches.size(); i++) {
            List<Long> batch = batches.get(i);
            
            try {
                calculator.recalculateForProducts(new HashSet<>(batch));
                log.info("Backfilled batch {}/{} ({} products)", 
                    i + 1, batches.size(), batch.size());
            } catch (Exception e) {
                log.error("Failed to backfill batch {}", i, e);
                // Continue with next batch
            }
            
            // Small delay to reduce load
            if (i < batches.size() - 1) {
                Thread.sleep(100);
            }
        }
        
        log.info("Backfill completed");
    }
}
```

### Step 2.2: Execute Backfill

```bash
# Enable backfill job temporarily
export JOBS_BACKFILL_ENABLED=true

# Run application (will execute on startup)
mvn spring-boot:run

# Monitor progress
watch -n 5 'psql -U postgres -d goodsprice -c "SELECT COUNT(*) FROM product_price_summaries;"'

# Expect: count increases to match product count
```

**Verification:**
```sql
-- Check summary count matches product count
SELECT 
    (SELECT COUNT(*) FROM products) as product_count,
    (SELECT COUNT(*) FROM product_price_summaries) as summary_count;

-- Should be equal (or summary slightly less if some products have no prices)
```

---

## Phase 3: Enable Batch Processing (Day 3-4)

### Step 3.1: Update PriceService to Track Changes

```java
@Service
@RequiredArgsConstructor
public class PriceService {
    
    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;
    
    @Transactional
    public Price createPrice(CreatePriceRequest request) {
        // 1. Save price (existing logic)
        Price price = priceRepository.save(mapper.toEntity(request));
        
        // 2. NEW: Mark product as having new prices
        // Just update timestamp - batch job will handle the rest!
        Product product = productRepository.findById(price.getProductId());
        product.setLastPriceUpdate(Instant.now());
        productRepository.save(product);
        
        return price;
    }
}
```

### Step 3.2: Enable Batch Scheduler

```yaml
# Enable batch job
features:
  price-summary:
    batch:
      enabled: true
      interval: 15m  # Run every 15 minutes (adjust as needed)
      batch-size: 100  # Process 100 products at a time
    notification:
      enabled: true
```

**Why Simple Batch?**
- **No events** - Just update timestamp
- **No queue** - Batch job queries database directly
- **Notification** - Users are notified when updates are ready

**Verification:**
```bash
# Create a new price via API
curl -X POST http://localhost:8080/prices \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "storeId": 1, "price": 5.99}'

# Check that timestamp was updated
psql -U postgres -d goodsprice -c \
  "SELECT id, last_price_update FROM products WHERE id = 1;"

# Wait 15 minutes (or run batch job manually)

# Check summary updated
psql -U postgres -d goodsprice -c \
  "SELECT * FROM product_price_summaries WHERE product_id = 1;"
```

---

## Phase 4: Validation (Day 4-5)

### Step 4.1: Data Consistency Check

```sql
-- Verify summaries match actual prices
WITH price_stats AS (
    SELECT 
        product_id,
        AVG(price) as actual_avg,
        MIN(price) as actual_min,
        MAX(price) as actual_max
    FROM prices
    GROUP BY product_id
)
SELECT 
    ps.product_id,
    ABS(ps.current_avg_price - price_stats.actual_avg) as avg_diff,
    ps.current_min_price = price_stats.actual_min as min_match,
    ps.current_max_price = price_stats.actual_max as max_match
FROM product_price_summaries ps
JOIN price_stats ON ps.product_id = price_stats.product_id
WHERE ABS(ps.current_avg_price - price_stats.actual_avg) > 0.01
   OR ps.current_min_price != price_stats.actual_min
   OR ps.current_max_price != price_stats.actual_max;

-- Should return 0 rows (all match)
```

### Step 4.2: Performance Comparison

```bash
# Test old API (direct aggregation)
time curl "http://localhost:8080/products?strategy=old"

# Test new API (summary table)
time curl "http://localhost:8080/products?strategy=new"

# Compare: New should be 5-10x faster
```

---

## Phase 5: Switch Over (Day 5)

### Step 5.1: Gradual Traffic Shift

```java
@Service
@RequiredArgsConstructor
public class ProductListService {
    
    @Value("${features.new-product-list.percentage:0}")
    private int newImplementationPercentage;
    
    private final OldProductService oldService;
    private final NewProductService newService;
    
    public Page<ProductDto> getProducts(Pageable pageable) {
        // Route traffic based on percentage
        if (shouldUseNewImplementation()) {
            return newService.getProducts(pageable);
        }
        return oldService.getProducts(pageable);
    }
    
    private boolean shouldUseNewImplementation() {
        int random = ThreadLocalRandom.current().nextInt(100);
        return random < newImplementationPercentage;
    }
}
```

### Step 5.2: Traffic Shift Schedule

| Time | New Implementation % | Monitor |
|------|---------------------|---------|
| 10:00 | 5% | Error rate, latency |
| 11:00 | 25% | Cache hit rate |
| 12:00 | 50% | DB CPU, memory |
| 13:00 | 75% | Full metrics |
| 14:00 | 100% | All systems |

### Step 5.3: Enable for 100%

```yaml
features:
  new-product-list:
    percentage: 100
```

**Verification:**
```bash
# Monitor for 1 hour
curl http://localhost:8080/actuator/metrics/product.list.query

# Should show: avg < 100ms, errors < 0.1%
```

---

## Phase 6: Cleanup (Day 6-7)

### Step 6.1: Remove Old Code

```java
// Delete OldProductService
// Delete aggregation queries from repository
// Remove feature flags (keep only new implementation)
```

### Step 6.2: Optimize

```sql
-- Add additional indexes based on query patterns
CREATE INDEX idx_price_summaries_category 
ON product_price_summaries(product_category) 
WHERE product_category IS NOT NULL;

-- Vacuum and analyze
VACUUM ANALYZE product_price_summaries;
```

### Step 6.3: Documentation

- Update API documentation
- Update runbooks
- Archive this migration document

---

## Rollback Procedures

### Scenario 1: New Code Has Bugs

```bash
# Immediate rollback: Disable new implementation
kubectl set env deployment/goodsprice \
  FEATURES_NEW-PRODUCT-LIST_PERCENTAGE=0

# Or update config and restart
vim application.yml  # Set percentage: 0
mvn spring-boot:run
```

### Scenario 2: Data Inconsistency

```sql
-- Recalculate all summaries
TRUNCATE TABLE product_price_summaries;

-- Re-run backfill job
-- (Application must have backfill job enabled)
```

### Scenario 3: Database Performance Issues

```sql
-- Disable batch job
UPDATE application_config 
SET features_price_summary_batch_job_enabled = false;

-- Drop indexes if causing write slowdown
DROP INDEX idx_price_summaries_covering;

-- Monitor and tune
```

---

## Monitoring During Migration

### Key Metrics Dashboard

```yaml
# Grafana dashboard configuration
panels:
  - title: "Migration Progress"
    targets:
      - query: 'sum(new_implementation_requests) / sum(total_requests)'
        legend: "New Implementation %"
  
  - title: "Summary Table Coverage"
    targets:
      - query: 'summary_row_count / product_count'
        legend: "Coverage %"
  
  - title: "Data Freshness"
    targets:
      - query: 'time() - max(summary_last_calculated_at)'
        legend: "Seconds since last update"
```

### Alert Thresholds

| Metric | Warning | Critical |
|--------|---------|----------|
| New implementation error rate | > 1% | > 5% |
| Summary coverage | < 95% | < 90% |
| Data freshness | > 10 min | > 30 min |
| Batch job failures | > 3/hour | > 10/hour |

---

## Timeline Summary

| Day | Phase | Activities | Risk |
|-----|-------|------------|------|
| 1 | Foundation | Create summary table, add timestamp columns | Low |
| 2 | Foundation | Deploy batch service code | Low |
| 3 | Backfill | Populate initial summaries | Medium |
| 4 | Enable | Enable batch job, test notifications | Medium |
| 5 | Validation | Consistency checks, performance test | Low |
| 6 | Switch Over | Use summary table for product list | Medium |
| 7 | Cleanup | Remove old aggregation code | Low |

---

## Success Criteria

✅ **Migration Successful If:**

- [ ] Zero downtime during switch
- [ ] Product list API < 100ms (P95)
- [ ] Data consistency 100%
- [ ] Error rate < 0.1%
- [ ] Can rollback within 5 minutes
- [ ] All old code removed

---

## Post-Migration Tasks

1. **Write retrospective document**
2. **Update architecture diagrams**
3. **Train team on new flow**
4. **Archive migration scripts**
5. **Celebrate! 🎉**

---

**Emergency Contacts:**
- Database Admin: [contact]
- DevOps: [contact]
- Product Owner: [contact]

**Escalation Path:**
1. Try rollback procedure
2. Contact on-call engineer
3. Page engineering manager if > 30 min impact
