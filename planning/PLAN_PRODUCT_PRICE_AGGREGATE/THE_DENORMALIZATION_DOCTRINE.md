# The Denormalization Doctrine 📚

## When and Why to Duplicate Data

> "Normalization is for academics. Denormalization is for production." 

This document explores when to break the rules of normalization for performance gains.

---

## The Problem: Joins Don't Scale

### The JOIN Penalty

```sql
-- Normalized approach (clean, but slow)
SELECT 
    p.id,
    p.name,
    p.category,
    ps.avg_price,
    ps.min_price,
    ps.max_price
FROM products p
LEFT JOIN product_price_summaries ps ON p.id = ps.product_id
WHERE p.status = 'ACTIVE'
ORDER BY p.name
LIMIT 10;

-- Still requires:
-- 1. Scan products table
-- 2. For each product, lookup in summary table
-- 3. Merge results
-- Time: ~50-100ms with indexes
```

### The Denormalized Solution (Ultra-Fast)

```sql
-- Single table, zero joins
SELECT *
FROM product_list_view  -- Denormalized materialized view
WHERE status = 'ACTIVE'
ORDER BY name
LIMIT 10;

-- Time: ~5-20ms
-- 5-10x faster!
```

---

## The Three Levels of Denormalization

### Level 1: Summary Table (Current Approach)

```sql
-- Separate table, requires JOIN
products ||--o| product_price_summaries
```

**Pros:**
- ✅ Clean separation of concerns
- ✅ Easy to maintain
- ✅ Can drop and rebuild

**Cons:**
- ❌ Still requires JOIN
- ❌ Two table scans

**Use when:**
- Moderate performance needs (< 200ms acceptable)
- Data consistency is critical
- Storage is limited

### Level 2: Wide Summary Table

```sql
-- Summary includes product info (redundant storage)
CREATE TABLE product_price_summaries_wide (
    product_id BIGINT PRIMARY KEY,
    
    -- Denormalized from products table
    product_name VARCHAR(255),
    product_category VARCHAR(100),
    product_brand VARCHAR(100),
    product_status VARCHAR(50),
    
    -- Aggregated from prices
    avg_price DECIMAL(10,2),
    min_price DECIMAL(10,2),
    max_price DECIMAL(10,2),
    store_count INT,
    
    -- Metadata
    last_updated TIMESTAMP
);

-- Single table query - no JOIN needed!
SELECT * FROM product_price_summaries_wide
WHERE product_status = 'ACTIVE'
ORDER BY product_name
LIMIT 10;
```

**Pros:**
- ✅ No JOINs
- ✅ Single index scan
- ✅ Very fast (< 20ms)

**Cons:**
- ❌ Data duplication (name stored in 2 places)
- ❌ Must sync when product changes
- ❌ More storage (~50% increase)

**Use when:**
- High read volume (1000+ queries/minute)
- Product data rarely changes
- Latency is critical

**Sync Strategy:**
```java
// When product name changes
@EventListener
public void onProductUpdated(ProductUpdatedEvent event) {
    // Update denormalized table
    summaryRepository.updateProductInfo(
        event.getProductId(),
        event.getNewName(),
        event.getNewCategory()
    );
}
```

### Level 3: Materialized View (PostgreSQL Specific)

```sql
-- Database-managed denormalization
CREATE MATERIALIZED VIEW product_list_view AS
SELECT 
    p.id,
    p.name,
    p.category,
    p.status,
    COALESCE(ps.avg_price, 0) as avg_price,
    COALESCE(ps.min_price, 0) as min_price,
    COALESCE(ps.max_price, 0) as max_price,
    COALESCE(ps.store_count, 0) as store_count,
    GREATEST(p.updated_at, ps.last_calculated_at) as last_updated
FROM products p
LEFT JOIN product_price_summaries ps ON p.id = ps.product_id
WHERE p.status = 'ACTIVE';

-- Create index on materialized view
CREATE INDEX idx_product_list_view_name 
ON product_list_view(name);

-- Refresh (can be concurrent to avoid locks)
REFRESH MATERIALIZED VIEW CONCURRENTLY product_list_view;
```

**Pros:**
- ✅ Database manages consistency
- ✅ Can use CONCURRENTLY (no locks)
- ✅ Zero application code

**Cons:**
- ❌ Full refresh is expensive
- ❌ Concurrent refresh requires unique index
- ❌ Not real-time (stale during refresh)

**Use when:**
- You have PostgreSQL
- Read-heavy workload
- Can tolerate 5-10 minute staleness

---

## Decision Flowchart

```
Product List Query Performance Needed?
    │
    ├── < 200ms acceptable? ──► Level 1: Summary Table ✅ (Recommended)
    │
    └── < 50ms required?
        │
        ├── Using PostgreSQL? ──► Level 3: Materialized View
        │
        └── Need real-time? ──► Level 2: Wide Summary Table
```

---

## Implementation: Wide Summary Table

If you choose Level 2 (recommended for best performance):

### 1. Table Schema

```sql
CREATE TABLE product_price_list_items (
    -- Primary key
    product_id BIGINT PRIMARY KEY,
    
    -- Denormalized product info
    product_name VARCHAR(255) NOT NULL,
    product_category VARCHAR(100),
    product_brand VARCHAR(100),
    product_unit VARCHAR(50),
    product_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    
    -- Aggregated price stats
    avg_price DECIMAL(10,2),
    min_price DECIMAL(10,2),
    max_price DECIMAL(10,2),
    latest_price DECIMAL(10,2),
    price_trend VARCHAR(20), -- 'UP', 'DOWN', 'STABLE'
    
    -- Store info
    store_count INT DEFAULT 0,
    cheapest_store_name VARCHAR(255),
    cheapest_store_price DECIMAL(10,2),
    
    -- Metadata
    price_count INT DEFAULT 0,
    first_price_date DATE,
    last_price_date DATE,
    last_updated TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Full-text search
    search_vector TSVECTOR,
    
    CONSTRAINT fk_product FOREIGN KEY (product_id) 
        REFERENCES products(id) ON DELETE CASCADE
);

-- Indexes for fast queries
CREATE INDEX idx_product_list_items_status 
    ON product_price_list_items(product_status);
CREATE INDEX idx_product_list_items_category 
    ON product_price_list_items(product_category);
CREATE INDEX idx_product_list_items_min_price 
    ON product_price_list_items(min_price) 
    WHERE product_status = 'ACTIVE';
CREATE INDEX idx_product_list_items_search 
    ON product_price_list_items USING GIN(search_vector);
```

### 2. Population Function

```sql
CREATE OR REPLACE FUNCTION refresh_product_list_item(p_product_id BIGINT)
RETURNS VOID AS $$
BEGIN
    INSERT INTO product_price_list_items (
        product_id,
        product_name,
        product_category,
        avg_price,
        min_price,
        max_price,
        store_count,
        price_count,
        last_price_date,
        last_updated
    )
    SELECT 
        p.id,
        p.name,
        p.category,
        AVG(pr.price),
        MIN(pr.price),
        MAX(pr.price),
        COUNT(DISTINCT pr.store_id),
        COUNT(pr.id),
        MAX(pr.date_recorded),
        NOW()
    FROM products p
    LEFT JOIN prices pr ON p.id = pr.product_id
    WHERE p.id = p_product_id
    GROUP BY p.id, p.name, p.category
    ON CONFLICT (product_id) DO UPDATE SET
        product_name = EXCLUDED.product_name,
        product_category = EXCLUDED.product_category,
        avg_price = EXCLUDED.avg_price,
        min_price = EXCLUDED.min_price,
        max_price = EXCLUDED.max_price,
        store_count = EXCLUDED.store_count,
        price_count = EXCLUDED.price_count,
        last_price_date = EXCLUDED.last_price_date,
        last_updated = NOW();
END;
$$ LANGUAGE plpgsql;
```

### 3. Java Entity

```java
@Entity
@Table(name = "product_price_list_items")
@Data
@Builder
public class ProductPriceListItem {
    
    @Id
    private Long productId;
    
    // Product info
    private String productName;
    private String productCategory;
    private String productBrand;
    private String productUnit;
    private String productStatus;
    
    // Price stats
    private BigDecimal avgPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal latestPrice;
    
    @Enumerated(EnumType.STRING)
    private PriceTrend trend;
    
    // Store info
    private Integer storeCount;
    private String cheapestStoreName;
    private BigDecimal cheapestStorePrice;
    
    // Metadata
    private Integer priceCount;
    private LocalDate firstPriceDate;
    private LocalDate lastPriceDate;
    private Instant lastUpdated;
}
```

### 4. Query Service

```java
@Service
@RequiredArgsConstructor
public class ProductListQueryService {
    
    private final ProductPriceListItemRepository repository;
    
    public Page<ProductListItemDto> findProducts(ProductListRequest request) {
        // Single table query - super fast!
        Specification<ProductPriceListItem> spec = Specification
            .where(statusEquals("ACTIVE"))
            .and(categoryEquals(request.getCategory()))
            .and(priceBetween(request.getMinPrice(), request.getMaxPrice()))
            .and(nameContains(request.getSearch()));
        
        return repository.findAll(spec, request.getPageable())
            .map(this::toDto);
    }
    
    private ProductListItemDto toDto(ProductPriceListItem item) {
        return ProductListItemDto.builder()
            .id(item.getProductId())
            .name(item.getProductName())
            .category(item.getProductCategory())
            .priceRange(PriceRangeDto.builder()
                .min(item.getMinPrice())
                .max(item.getMaxPrice())
                .avg(item.getAvgPrice())
                .build())
            .storeCount(item.getStoreCount())
            .trend(item.getTrend())
            .build();
    }
}
```

---

## Storage Cost Analysis

Assuming 1 million products, 10 million prices:

| Approach | Storage Increase | Query Time | Complexity |
|----------|------------------|------------|------------|
| No Summary | Baseline | 2000ms | Low |
| Summary Table | +50MB | 100ms | Medium |
| Wide Summary | +200MB | 20ms | High |
| Materialized View | +150MB | 15ms | Low |

**Recommendation:** Wide Summary Table (Level 2) gives best performance/cost ratio.

---

## Consistency Guarantees

### Eventual Consistency (Default)

```
Price Updated
    │
    ├─► Immediate: Price saved ✓
    │
    ├─► Within 2 min: Summary updated ✓
    │
    └─► User sees: Old or New value (acceptable)
```

### Strong Consistency (If Needed)

```java
@Transactional
public Price createPrice(CreatePriceRequest request) {
    // 1. Save price
    Price price = priceRepository.save(entity);
    
    // 2. IMMEDIATELY update summary (blocking)
    summaryService.recalculateAndSave(price.getProductId());
    
    // 3. Return
    return price;
}

// Trade-off: Slower writes (~50ms penalty)
```

---

## Conclusion

**For your use case (product list with pagination):**

Start with **Level 1: Summary Table** (THE_GRAND_AGGREGATION_STRATEGY.md)

If performance isn't sufficient (< 50ms), escalate to **Level 2: Wide Summary Table**

**Key Takeaway:** 
- Normalization = Write optimized
- Denormalization = Read optimized
- Your UI needs reads → Denormalize wisely
