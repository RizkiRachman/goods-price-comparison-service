package com.example.goodsprice.price.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PriceMapperTest {

  private PriceMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new PriceMapperImpl();
  }

  @Test
  @DisplayName("Should map domain to entity")
  void shouldMapDomainToEntity() {
    PriceDomain domain =
        PriceDomain.builder()
            .id(1L)
            .productId(100L)
            .storeId(10L)
            .price(15000.0)
            .unitPrice(15000.0)
            .dateRecorded(LocalDate.of(2026, 6, 1))
            .isPromo(false)
            .build();

    PriceEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals(1L, entity.getId());
    assertEquals(100L, entity.getProductId());
    assertEquals(10L, entity.getStoreId());
    assertEquals(15000.0, entity.getPrice());
    assertEquals(15000.0, entity.getUnitPrice());
    assertEquals(LocalDate.of(2026, 6, 1), entity.getDateRecorded());
    assertEquals(false, entity.getIsPromo());
  }

  @Test
  @DisplayName("Should map entity to domain")
  void shouldMapEntityToDomain() {
    PriceEntity entity = new PriceEntity();
    entity.setId(1L);
    entity.setProductId(100L);
    entity.setStoreId(10L);
    entity.setPrice(15000.0);
    entity.setUnitPrice(14000.0);
    entity.setDateRecorded(LocalDate.of(2026, 6, 1));
    entity.setIsPromo(true);

    PriceDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals(1L, domain.getId());
    assertEquals(100L, domain.getProductId());
    assertEquals(10L, domain.getStoreId());
    assertEquals(15000.0, domain.getPrice());
    assertEquals(14000.0, domain.getUnitPrice());
    assertEquals(LocalDate.of(2026, 6, 1), domain.getDateRecorded());
    assertEquals(true, domain.getIsPromo());
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
    PriceDomain domain = PriceDomain.builder().build();

    PriceEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertNull(entity.getId());
    assertNull(entity.getProductId());
    assertNull(entity.getStoreId());
    assertNull(entity.getPrice());
    assertNull(entity.getDateRecorded());
    assertNull(entity.getIsPromo());
  }

  @Test
  @DisplayName("Should handle null fields in entity")
  void shouldHandleNullFieldsInEntity() {
    PriceEntity entity = new PriceEntity();

    PriceDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertNull(domain.getId());
    assertNull(domain.getProductId());
    assertNull(domain.getPrice());
  }
}
