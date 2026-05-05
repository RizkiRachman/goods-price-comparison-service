# Query Performance Battle Plan ⚔️

## Benchmarks, Bottlenecks, and Victory

This document establishes performance targets and optimization strategies for the product list API.

---

## Current State Analysis

### The N+1 Problem

```java
// BAD: N+1 queries
public List<ProductDto> getProducts() {
    List<Product> products = productRepository.findAll(); // 1 query
    
    for (Product p : products) {
        // This runs N times!
        PriceStats stats = priceService.getStats(p.getId()); // N queries
        dtos.add(new ProductDto(p, stats));
    }
}
// Total: 1 + N queries (11 queries for 10 products!)
```

### The Aggregation Problem

```sql
-- BAD: Aggregation on every request
SELECT p.*, 
       AVG(pr.price) as avg_price,
       MIN(pr.price) as min_price,
       MAX(pr.price) as max_price
FROM products p
LEFT JOIN prices pr ON p.id = pr.product_id
GROUP BY p.id
LIMIT 10;
-- Scans ALL prices, groups, then limits
```

---

## Performance Targets

### SLAs (Service Level Agreements)

| Metric | Current | Target | Stretch |
|--------|---------|--------|---------|
| Product List (10 items) | 500-2000ms | < 100ms | < 50ms |
| Product List (50 items) | 3000ms+ | < 200ms | < 100ms |
| DB Queries per Request | 31 | 1-2 | 1 |
| Cache Hit Rate | 0% | 80% | 95% |
| P95 Latency | 3000ms | 150ms | 75ms |
| Error Rate | < 1% | < 0.1% | < 0.01% |

### Load Targets

| Metric | Target |
|--------|--------|
| Requests/Second | 1000+ |
| Concurrent Users | 10,000+ |
| Database Connections | < 20 |
| Memory Usage | < 2GB |

---

## Performance Testing Strategy

### 1. Load Test Scenarios

```java
// Using JMeter or k6

@Test
public void testProductListPerformance() {
    // Given: Database with 1M products, 10M prices
    
    // When: 100 concurrent users request product list
    LoadTestResult result = LoadTest.builder()
        .endpoint("/api/products?page=0&size=10")
        .concurrentUsers(100)
        .duration(Duration.ofMinutes(5))
        .rampUp(Duration.ofSeconds(30))
        .run();
    
    // Then: Performance meets SLA
    assertThat(result.getAverageResponseTime()).isLessThan(100);
    assertThat(result.getP95ResponseTime()).isLessThan(150);
    assertThat(result.getErrorRate()).isLessThan(0.001);
}
```

### 2. Database Load Test

```sql
-- Simulate load
EXPLAIN ANALYZE
SELECT p.id, p.name, ps.avg_price
FROM products p
LEFT JOIN product_price_summaries ps ON p.id = ps.product_id
ORDER BY p.name
LIMIT 10 OFFSET 0;

-- Check execution plan:
-- Should show: Index Scan, Nested Loop, not Seq Scan
```

---

## Optimization Phases

### Phase 1: Query Optimization (Week 1)

**Goal:** Reduce query count from 31 to 2

#### 1.1 Batch Fetch Summaries

```java
// BEFORE: N+1
for (Product p : products) {
    PriceStats stats = priceService.getStats(p.getId()); // 1 query each
}

// AFTER: Batch (1 query)
Set<Long> productIds = products.stream()
    .map(Product::getId)
    .collect(Collectors.toSet());

Map<Long, PriceStats> statsMap = priceService
    .getStatsForProducts(productIds); // 1 query
```

#### 1.2 Optimize SQL

```sql
-- Add composite index for common queries
CREATE INDEX idx_products_status_name 
ON products(status, name);

-- Covering index for summary table
CREATE INDEX idx_price_summaries_covering 
ON product_price_summaries(product_id, min_price, max_price, avg_price);
```

**Expected Improvement:** 500ms → 150ms

### Phase 2: Caching (Week 2)

**Goal:** Sub-50ms for cache hits

#### 2.1 Redis Cache

```java
@Service
@RequiredArgsConstructor
public class CachedProductService {
    
    private final ProductRepository productRepository;
    private final PriceSummaryRepository summaryRepository;
    private final RedisTemplate<String, ProductListDto> redisTemplate;
    
    private static final String CACHE_KEY = "products:list:%s:%s";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    
    @Cacheable(value = "productList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductListDto> getProducts(Pageable pageable) {
        // Try cache first
        String cacheKey = String.format(CACHE_KEY, 
            pageable.getPageNumber(), 
            pageable.getPageSize());
        
        Page<ProductListDto> cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // Fetch from DB
        Page<ProductListDto> result = fetchFromDatabase(pageable);
        
        // Cache result
        redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL);
        
        return result;
    }
    
    // Invalidate cache when prices change
    @EventListener
    public void onPriceUpdated(PriceUpdatedEvent event) {
        // Clear all product list caches
        redisTemplate.delete(redisTemplate.keys("products:list:*"));
    }
}
```

#### 2.2 Application-Level Cache

```java
@Component
public class ProductSummaryCache {
    
    // Caffeine cache (in-memory, per instance)
    private final Cache<Long, PriceSummaryDto> cache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .recordStats()
        .build();
    
    public PriceSummaryDto get(Long productId) {
        return cache.get(productId, this::loadFromDb);
    }
    
    public void invalidate(Long productId) {
        cache.invalidate(productId);
    }
}
```

**Expected Improvement:** 150ms → 20ms (cache hit)

### Phase 3: Denormalization (Week 3-4)

**Goal:** Single table query

If Phases 1-2 don't meet < 50ms target, implement wide summary table:

```sql
-- Denormalized table (no joins needed)
CREATE TABLE product_price_list_view (
    product_id BIGINT PRIMARY KEY,
    product_name VARCHAR(255),
    product_category VARCHAR(100),
    avg_price DECIMAL(10,2),
    min_price DECIMAL(10,2),
    max_price DECIMAL(10,2),
    store_count INT,
    last_updated TIMESTAMP
);

-- Ultra-fast query
SELECT * FROM product_price_list_view
WHERE product_category = 'Dairy'
ORDER BY product_name
LIMIT 10;
-- Time: < 10ms
```

**Expected Improvement:** 150ms → 20ms (no cache), < 5ms (with cache)

---

## Monitoring & Alerting

### Metrics to Track

```yaml
# application.yml
management:
  metrics:
    enabled: true
    export:
      prometheus:
        enabled: true
  endpoints:
    web:
      exposure:
        include: metrics,prometheus
```

### Key Metrics

```java
@Component
@RequiredArgsConstructor
public class PerformanceMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public void recordProductListQuery(long durationMs, int itemCount) {
        meterRegistry.timer("product.list.query")
            .record(durationMs, TimeUnit.MILLISECONDS);
        
        meterRegistry.counter("product.list.items", 
            "count", String.valueOf(itemCount));
    }
    
    public void recordCacheHit() {
        meterRegistry.counter("product.list.cache", 
            "result", "hit").increment();
    }
    
    public void recordCacheMiss() {
        meterRegistry.counter("product.list.cache", 
            "result", "miss").increment();
    }
}
```

### Alerts

```yaml
# alerting-rules.yml
groups:
  - name: product-list-performance
    rules:
      - alert: ProductListSlow
        expr: histogram_quantile(0.95, 
          rate(product_list_query_seconds_bucket[5m])) > 0.2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Product list API is slow"
          
      - alert: ProductListVerySlow
        expr: histogram_quantile(0.95, 
          rate(product_list_query_seconds_bucket[5m])) > 0.5
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Product list API is very slow!"
```

---

## Bottleneck Detection

### Database Slow Query Log

```sql
-- PostgreSQL configuration
log_min_duration_statement = 100  -- Log queries > 100ms
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '
```

### Application Profiling

```bash
# Enable JVM profiling
java -jar -agentlib:hprof=cpu=samples,depth=20 application.jar

# Or use async-profiler
./profiler.sh -d 30 -f profile.html <pid>
```

### Common Bottlenecks

| Symptom | Likely Cause | Solution |
|---------|--------------|----------|
| High CPU, low DB | Inefficient code | Profile and optimize |
| High DB CPU | Missing indexes | Add indexes |
| Connection pool exhausted | Long-running queries | Optimize queries or increase pool |
| Memory spikes | Large result sets | Pagination or streaming |
| Cache misses | Cache too small | Increase cache size |

---

## Rollback Plan

If optimizations cause issues:

```java
@Component
public class ProductListFeatureFlag {
    
    @Value("${feature.newProductList.enabled:false}")
    private boolean newImplementationEnabled;
    
    private final OldProductService oldService;
    private final NewProductService newService;
    
    public Page<ProductDto> getProducts(Pageable pageable) {
        if (newImplementationEnabled) {
            try {
                return newService.getProducts(pageable);
            } catch (Exception e) {
                log.error("New implementation failed, falling back", e);
                return oldService.getProducts(pageable);
            }
        }
        return oldService.getProducts(pageable);
    }
}
```

---

## Success Criteria

✅ **Phase 1 Complete:**
- Product list < 200ms
- < 5 queries per request
- No N+1 issues

✅ **Phase 2 Complete:**
- Cache hit rate > 70%
- P95 < 100ms
- Memory usage stable

✅ **Phase 3 Complete (if needed):**
- P95 < 50ms
- Supports 1000+ RPS
- Database CPU < 50%

---

## Testing Checklist

Before going live:

- [ ] Load test with 1000 concurrent users
- [ ] Database performance test with full dataset
- [ ] Cache hit rate > 70%
- [ ] No memory leaks (run for 24 hours)
- [ ] Error rate < 0.1%
- [ ] Rollback plan tested
- [ ] Monitoring dashboards created
- [ ] Alerts configured

---

**War cry:** *"Make it work, make it right, make it fast — in that order!"*
