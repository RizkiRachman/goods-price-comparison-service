# Product Price Aggregation - Master Plan 📊

## Executive Overview

**Objective:** Implement pre-computed price statistics (avg, min, max) for fast product list queries with pagination support.

**Problem:** Current N+1 query pattern causes 500-2000ms response times for product list API.

**Solution:** **Simplified Batch-Only Architecture** with notifications - no events, no queues, no complexity.

---

## At a Glance

```
┌─────────────────────────────────────────────────────────────────┐
│              SIMPLIFIED BATCH-ONLY ARCHITECTURE                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Price Created                                                 │
│       │                                                         │
│       ├──► Save to prices table                                 │
│       │                                                         │
│       └──► Update product.last_price_update timestamp           │
│                     │                                           │
│                     │                                           │
│                     │ Every 15-30 minutes                       │
│                     ▼                                           │
│              ┌──────────────┐                                   │
│              │ Batch Job    │                                   │
│              │ 1. Find pro- │                                   │
│              │    ducts with│                                   │
│              │    new prices│                                   │
│              │ 2. Calculate │                                   │
│              │    summaries │                                   │
│              │ 3. Notify    │                                   │
│              │    users     │                                   │
│              └──────┬───────┘                                   │
│                     │                                           │
│                     ▼                                           │
│       ┌─────────────────────────┐                               │
│       │ product_price_summaries │                               │
│       │ updated + notifications │                               │
│       │ sent                    │                               │
│       └─────────────────────────┘                               │
│                     │                                           │
│                     ▼                                           │
│       Fast product list queries (< 100ms)                       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

> **Key Design Decision:** **NO events, NO queues, NO complex schedulers.** Just a simple batch job that runs every 15-30 minutes. Users are **notified** when updates are available, making the delay transparent and acceptable.

**Result:** 10-20x faster product list queries with minimal complexity.

---

## Document Structure

| Document | Purpose | Audience |
|----------|---------|----------|
| **[PLAN_PRODUCT_PRICE_AGGREGATE/THE_GRAND_AGGREGATION_STRATEGY.md](PLAN_PRODUCT_PRICE_AGGREGATE/THE_GRAND_AGGREGATION_STRATEGY.md)** | Complete implementation guide with code | Developers implementing the feature |
| **[PLAN_PRODUCT_PRICE_AGGREGATE/THE_DENORMALIZATION_DOCTRINE.md](PLAN_PRODUCT_PRICE_AGGREGATE/THE_DENORMALIZATION_DOCTRINE.md)** | Database design patterns & trade-offs | Architects & DBAs |
| **[PLAN_PRODUCT_PRICE_AGGREGATE/QUERY_PERFORMANCE_BATTLE_PLAN.md](PLAN_PRODUCT_PRICE_AGGREGATE/QUERY_PERFORMANCE_BATTLE_PLAN.md)** | Performance optimization & monitoring | DevOps & Performance Engineers |
| **[PLAN_PRODUCT_PRICE_AGGREGATE/THE_MIGRATION_BLUEPRINT.md](PLAN_PRODUCT_PRICE_AGGREGATE/THE_MIGRATION_BLUEPRINT.md)** | Zero-downtime deployment guide | Release Engineers |
| **[PLAN_PRODUCT_PRICE_AGGREGATE/README.md](PLAN_PRODUCT_PRICE_AGGREGATE/README.md)** | Index & quick navigation | Everyone |

---

## Quick Decision Guide

```
Starting implementation?
    ↓
    Read THE_GRAND_AGGREGATION_STRATEGY.md
    (Has all the code you need)

Deploying to production?
    ↓
    Read THE_MIGRATION_BLUEPRINT.md
    (Step-by-step zero-downtime guide)

Optimizing performance?
    ↓
    Read QUERY_PERFORMANCE_BATTLE_PLAN.md
    (Caching, indexing, monitoring)

Designing database schema?
    ↓
    Read THE_DENORMALIZATION_DOCTRINE.md
    (Trade-offs & patterns)
```

---

## The Problem

### Current State (Slow)

```sql
-- UI requests: "Show 10 products with price stats"
SELECT p.*,
       (SELECT AVG(price) FROM prices WHERE product_id = p.id),
       (SELECT MIN(price) FROM prices WHERE product_id = p.id),
       (SELECT MAX(price) FROM prices WHERE product_id = p.id)
FROM products p
LIMIT 10;

-- Result: 31 queries, 500-2000ms ❌
```

### Target State (Fast)

```sql
-- Single JOIN with pre-computed summary
SELECT p.id, p.name, ps.avg_price, ps.min_price, ps.max_price
FROM products p
LEFT JOIN product_price_summaries ps ON p.id = ps.product_id
LIMIT 10;

-- Result: 1-2 queries, < 100ms ✅
```

---

## Why Simplified Batch-Only?

### Comparison: Architecture Approaches

| Approach | Components | Delay | Complexity | Best For |
|----------|-----------|-------|------------|----------|
| **Complex** (Events+Queue+2 Schedulers) | 6+ classes, Events, Queue | 2 min | High | High volume, real-time needs |
| **Medium** (Events+Immediate) | 4 classes, Events | Seconds | Medium | Low volume, fast updates |
| **✅ Simplified** (Batch Only + Notify) | 3 classes | 15-30 min | **Low** | **Our case!** No real-time needed |

### Why This Fits Perfectly

1. **No Real-Time Requirement** - Users don't need instant price updates
2. **Notification Available** - Users are **notified** when updates are ready
3. **Much Simpler Code** - Single batch job vs. complex event infrastructure
4. **Easier Maintenance** - Fewer moving parts
5. **Better Resource Usage** - No constant event processing
6. **Transparent UX** - Users KNOW when data is fresh

---

## Key Components

### 1. New Table: `product_price_summaries`

```sql
CREATE TABLE product_price_summaries (
    product_id BIGINT PRIMARY KEY,
    
    -- Price statistics
    avg_price DECIMAL(10,2),
    min_price DECIMAL(10,2),
    max_price DECIMAL(10,2),
    store_count INT,
    price_count INT,
    
    -- Metadata
    last_calculated_at TIMESTAMP NOT NULL
);
```

### 2. Track Price Updates

```java
@Service
public class PriceService {
    
    @Transactional
    public Price createPrice(CreatePriceRequest request) {
        // 1. Save price
        Price price = priceRepository.save(mapper.toEntity(request));
        
        // 2. Just update a timestamp - no events, no complexity!
        Product product = productRepository.findById(price.getProductId());
        product.setLastPriceUpdate(Instant.now());
        productRepository.save(product);
        
        return price;
    }
}
```

### 3. Simple Batch Job

```java
@Service
public class PriceSummaryBatchService {
    
    @Scheduled(fixedDelay = 15, TimeUnit.MINUTES)
    public void updateSummaries() {
        // 1. Find products with new prices
        List<Product> products = productRepository
            .findProductsNeedingSummaryUpdate();
        
        // 2. Update summaries
        for (Product product : products) {
            updateProductSummary(product);
        }
        
        // 3. Notify users
        notificationService.notifyPriceUpdatesAvailable(products);
    }
}
```

### 4. Notification System

```java
@Service
public class NotificationService {
    
    public void notifyPriceUpdatesAvailable(List<Product> products) {
        NotificationEvent event = NotificationEvent.builder()
            .type("PRICE_UPDATES_AVAILABLE")
            .title("New Price Data Available")
            .message(products.size() + " products have updated prices")
            .productIds(products.stream().map(Product::getId).toList())
            .timestamp(Instant.now())
            .build();
        
        notificationPort.send(event);
    }
}
```

---

## User Experience Flow

### Before (Complex Real-Time)
```
User: Uploads receipt
System: Processes immediately (2 min)
User: Refreshes page, sees updates
Problem: User doesn't know WHEN to refresh
```

### After (Simplified + Notification)
```
User: Uploads receipt
System: Queues for batch processing
User: Continues browsing
System (15-30 min later): Updates summaries + sends notification
User: Sees notification "15 products updated"
User: Clicks notification, sees fresh data
Benefit: User KNOWS when data is ready!
```

---

## Performance Targets

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| **Product List (10 items)** | 500-2000ms | < 100ms | **10-20x faster** |
| **DB Queries** | 31 | 1-2 | **15x fewer** |
| **P95 Latency** | 3000ms | 150ms | **20x better** |
| **Code Complexity** | High | Low | **Much simpler** |

**Trade-off:** 15-30 minute data staleness (acceptable with notifications)

---

## ERD Changes

### Before
```
products ||--o{ prices
stores ||--o{ prices
```

### After
```
products ||--o{ prices
products ||--o| product_price_summaries
stores ||--o{ prices
```

---

## Implementation Timeline

| Phase | Duration | Key Activities |
|-------|----------|----------------|
| **1. Database** | 1 day | Create summary table, add timestamp columns |
| **2. Batch Service** | 2 days | Implement batch job, calculation logic |
| **3. Notifications** | 1 day | Add notification service, UI integration |
| **4. Testing** | 2 days | Test batch job, verify calculations |
| **5. Migration** | 2 days | Zero-downtime deployment, backfill |
| **6. Cleanup** | 1 day | Remove old aggregation code |
| **Total** | **~9 days** | |

---

## Risk Assessment

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Data inconsistency | High | Low | Batch job calculates from source of truth |
| Batch job failure | Medium | Low | Hourly retry, monitoring alerts |
| Long batch time | Medium | Medium | Batch size limits, pagination |
| User confusion | Low | Medium | Clear notifications, "last updated" timestamp |

---

## Frequently Asked Questions

**Q: Why not use real-time updates?**  
A: Not needed! Users are happy with 15-30 minute updates when notified. Much simpler architecture.

**Q: What if user needs immediate price info?**  
A: They can still view individual prices in real-time. Only the aggregate stats (avg/min/max) are batched.

**Q: How do users know data is fresh?**  
A: Notification system + "Last updated: 15 minutes ago" timestamp in UI.

**Q: Can we adjust the batch frequency?**  
A: Yes! Change `@Scheduled(fixedDelay = X)` to 5 min, 30 min, or 1 hour based on needs.

**Q: What if batch job takes too long?**  
A: Process in smaller batches (100 at a time), add pagination, or run more frequently.

---

## Next Steps

1. **Review THE_GRAND_AGGREGATION_STRATEGY.md** - Complete implementation details
2. **Discuss with team** - Confirm 15-30 min delay is acceptable
3. **Design notification UX** - How will users be notified?
4. **Create tickets** - Break down by phases
5. **Start Phase 1** - Database migration

---

## Related Documentation

- [Architecture Overview](../docs/ARCHITECTURE_HYBRID.md)
- [Database ERD](../docs/ERD.md)
- [Developer Guide](../docs/DEVELOPER_GUIDE.md)
- [API Documentation](../README.md#-documentation)

---

<div align="center">

**Ready to implement? Start with [THE_GRAND_AGGREGATION_STRATEGY.md](PLAN_PRODUCT_PRICE_AGGREGATE/THE_GRAND_AGGREGATION_STRATEGY.md)**

*Keep it simple, notify the user, ship faster!* 🚀

</div>
