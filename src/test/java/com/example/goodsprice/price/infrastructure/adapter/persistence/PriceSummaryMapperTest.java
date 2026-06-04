package com.example.goodsprice.price.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceSummaryEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PriceSummaryMapperTest {

  private PriceSummaryMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new PriceSummaryMapperImpl();
  }

  @Test
  @DisplayName("Should map domain to entity")
  void shouldMapDomainToEntity() {
    ProductPriceSummary domain =
        ProductPriceSummary.builder()
            .productId(1L)
            .avgPrice(new BigDecimal("15.50"))
            .minPrice(new BigDecimal("10.00"))
            .maxPrice(new BigDecimal("20.00"))
            .storeCount(3)
            .priceCount(10)
            .lastCalculatedAt(LocalDateTime.of(2024, 4, 1, 12, 0))
            .lastPriceDate(LocalDate.of(2024, 3, 28))
            .build();

    PriceSummaryEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals(1L, entity.getProductId());
    assertEquals(new BigDecimal("15.50"), entity.getAvgPrice());
    assertEquals(new BigDecimal("10.00"), entity.getMinPrice());
    assertEquals(new BigDecimal("20.00"), entity.getMaxPrice());
    assertEquals(3, entity.getStoreCount());
    assertEquals(10, entity.getPriceCount());
    assertEquals(LocalDateTime.of(2024, 4, 1, 12, 0), entity.getLastCalculatedAt());
    assertEquals(LocalDate.of(2024, 3, 28), entity.getLastPriceDate());
  }

  @Test
  @DisplayName("Should map entity to domain")
  void shouldMapEntityToDomain() {
    PriceSummaryEntity entity = new PriceSummaryEntity();
    entity.setProductId(1L);
    entity.setAvgPrice(new BigDecimal("15.50"));
    entity.setMinPrice(new BigDecimal("10.00"));
    entity.setMaxPrice(new BigDecimal("20.00"));
    entity.setStoreCount(3);
    entity.setPriceCount(10);
    entity.setLastCalculatedAt(LocalDateTime.of(2024, 4, 1, 12, 0));
    entity.setLastPriceDate(LocalDate.of(2024, 3, 28));

    ProductPriceSummary domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals(1L, domain.getProductId());
    assertEquals(new BigDecimal("15.50"), domain.getAvgPrice());
    assertEquals(new BigDecimal("10.00"), domain.getMinPrice());
    assertEquals(new BigDecimal("20.00"), domain.getMaxPrice());
    assertEquals(3, domain.getStoreCount());
    assertEquals(10, domain.getPriceCount());
    assertEquals(LocalDateTime.of(2024, 4, 1, 12, 0), domain.getLastCalculatedAt());
    assertEquals(LocalDate.of(2024, 3, 28), domain.getLastPriceDate());
  }

  @Test
  @DisplayName("Should return null when mapping null domain to entity")
  void shouldReturnNullWhenMappingNullDomainToEntity() {
    assertNull(mapper.toEntity(null));
  }

  @Test
  @DisplayName("Should return null when mapping null entity to domain")
  void shouldReturnNullWhenMappingNullEntityToDomain() {
    assertNull(mapper.toDomain(null));
  }

  @Test
  @DisplayName("Should handle null fields in domain")
  void shouldHandleNullFieldsInDomain() {
    ProductPriceSummary domain =
        ProductPriceSummary.builder()
            .productId(1L)
            .avgPrice(null)
            .minPrice(null)
            .maxPrice(null)
            .lastCalculatedAt(LocalDateTime.now())
            .build();

    PriceSummaryEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals(1L, entity.getProductId());
    assertNull(entity.getAvgPrice());
    assertNull(entity.getMinPrice());
    assertNull(entity.getMaxPrice());
    assertNotNull(entity.getLastCalculatedAt());
  }

  @Test
  @DisplayName("Should handle null fields in entity")
  void shouldHandleNullFieldsInEntity() {
    PriceSummaryEntity entity = new PriceSummaryEntity();
    entity.setProductId(1L);
    entity.setLastCalculatedAt(LocalDateTime.now());

    ProductPriceSummary domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals(1L, domain.getProductId());
    assertNull(domain.getAvgPrice());
    assertNotNull(domain.getLastCalculatedAt());
  }

  @Test
  @DisplayName("Should handle empty builder domain")
  void shouldHandleEmptyBuilderDomain() {
    ProductPriceSummary domain = ProductPriceSummary.builder().build();

    PriceSummaryEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertNull(entity.getProductId());
    assertNull(entity.getAvgPrice());
    assertNull(entity.getLastCalculatedAt());
  }
}
