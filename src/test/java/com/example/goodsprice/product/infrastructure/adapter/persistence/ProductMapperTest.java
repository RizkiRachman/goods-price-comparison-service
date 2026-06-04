package com.example.goodsprice.product.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

  private ProductMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ProductMapperImpl();
  }

  @Test
  @DisplayName("Should map domain to entity")
  void shouldMapDomainToEntity() {
    var domain =
        ProductDomain.builder()
            .id(1L)
            .name("Susu Kotak")
            .category("Minuman")
            .brand("Indomilk")
            .unit("KG")
            .status("ACTIVE")
            .lastPriceUpdate(LocalDateTime.of(2024, 4, 1, 12, 0))
            .summaryLastCalculated(LocalDateTime.of(2024, 4, 2, 12, 0))
            .build();

    ProductEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals(1L, entity.getId());
    assertEquals("Susu Kotak", entity.getName());
    assertEquals("Minuman", entity.getCategory());
    assertEquals("Indomilk", entity.getBrand());
    assertEquals("KG", entity.getUnit());
    assertEquals("ACTIVE", entity.getStatus());
    assertEquals(LocalDateTime.of(2024, 4, 1, 12, 0), entity.getLastPriceUpdate());
    assertEquals(LocalDateTime.of(2024, 4, 2, 12, 0), entity.getSummaryLastCalculated());
  }

  @Test
  @DisplayName("Should map entity to domain")
  void shouldMapEntityToDomain() {
    var entity = new ProductEntity();
    entity.setId(1L);
    entity.setName("Susu Kotak");
    entity.setCategory("Minuman");
    entity.setBrand("Indomilk");
    entity.setUnit("KG");
    entity.setStatus("ACTIVE");
    entity.setLastPriceUpdate(LocalDateTime.of(2024, 4, 1, 12, 0));
    entity.setSummaryLastCalculated(LocalDateTime.of(2024, 4, 2, 12, 0));

    ProductDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals(1L, domain.getId());
    assertEquals("Susu Kotak", domain.getName());
    assertEquals("Minuman", domain.getCategory());
    assertEquals("Indomilk", domain.getBrand());
    assertEquals("KG", domain.getUnit());
    assertEquals("ACTIVE", domain.getStatus());
    assertEquals(LocalDateTime.of(2024, 4, 1, 12, 0), domain.getLastPriceUpdate());
    assertEquals(LocalDateTime.of(2024, 4, 2, 12, 0), domain.getSummaryLastCalculated());
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
    var domain = ProductDomain.builder().name("Susu Kotak").build();

    ProductEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals("Susu Kotak", entity.getName());
    assertNull(entity.getId());
    assertNull(entity.getCategory());
    assertNull(entity.getBrand());
    assertNull(entity.getUnit());
    assertNull(entity.getStatus());
  }

  @Test
  @DisplayName("Should handle null fields in entity")
  void shouldHandleNullFieldsInEntity() {
    var entity = new ProductEntity();
    entity.setName("Susu Kotak");

    ProductDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals("Susu Kotak", domain.getName());
    assertNull(domain.getId());
    assertNull(domain.getCategory());
    assertNull(domain.getBrand());
    assertNull(domain.getUnit());
    assertNull(domain.getStatus());
  }
}
