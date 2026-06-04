package com.example.goodsprice.category.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryMapperTest {

  private CategoryMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CategoryMapperImpl();
  }

  @Test
  @DisplayName("Should map domain to entity")
  void shouldMapDomainToEntity() {
    var domain =
        CategoryDomain.builder()
            .id("FRUIT")
            .name("Fruits")
            .description("All kinds of fruits")
            .status("ACTIVE")
            .build();

    CategoryEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals("FRUIT", entity.getId());
    assertEquals("Fruits", entity.getName());
    assertEquals("All kinds of fruits", entity.getDescription());
    assertEquals("ACTIVE", entity.getStatus());
  }

  @Test
  @DisplayName("Should map entity to domain")
  void shouldMapEntityToDomain() {
    var entity = new CategoryEntity();
    entity.setId("FRUIT");
    entity.setName("Fruits");
    entity.setDescription("All kinds of fruits");
    entity.setStatus("ACTIVE");

    CategoryDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals("FRUIT", domain.getId());
    assertEquals("Fruits", domain.getName());
    assertEquals("All kinds of fruits", domain.getDescription());
    assertEquals("ACTIVE", domain.getStatus());
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
    var domain = CategoryDomain.builder().id("FRUIT").name("Fruits").build();

    CategoryEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals("FRUIT", entity.getId());
    assertEquals("Fruits", entity.getName());
    assertNull(entity.getDescription());
    assertNull(entity.getStatus());
  }

  @Test
  @DisplayName("Should handle null fields in entity")
  void shouldHandleNullFieldsInEntity() {
    var entity = new CategoryEntity();
    entity.setId("FRUIT");
    entity.setName("Fruits");

    CategoryDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals("FRUIT", domain.getId());
    assertEquals("Fruits", domain.getName());
    assertNull(domain.getDescription());
    assertNull(domain.getStatus());
  }
}
