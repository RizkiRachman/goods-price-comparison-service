package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.unit.application.domain.model.UnitDomain;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnitMapperTest {

  private UnitMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new UnitMapperImpl();
  }

  @Test
  @DisplayName("Should map domain to entity")
  void shouldMapDomainToEntity() {
    var domain =
        UnitDomain.builder()
            .id("KG")
            .name("Kilogram")
            .symbol("kg")
            .type(UnitType.WEIGHT)
            .description("Unit of mass")
            .status("ACTIVE")
            .build();

    UnitEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals("KG", entity.getId());
    assertEquals("Kilogram", entity.getName());
    assertEquals("kg", entity.getSymbol());
    assertEquals(UnitType.WEIGHT, entity.getType());
    assertEquals("Unit of mass", entity.getDescription());
    assertEquals("ACTIVE", entity.getStatus());
  }

  @Test
  @DisplayName("Should map entity to domain")
  void shouldMapEntityToDomain() {
    var entity = new UnitEntity();
    entity.setId("KG");
    entity.setName("Kilogram");
    entity.setSymbol("kg");
    entity.setType(UnitType.WEIGHT);
    entity.setDescription("Unit of mass");
    entity.setStatus("ACTIVE");

    UnitDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals("KG", domain.getId());
    assertEquals("Kilogram", domain.getName());
    assertEquals("kg", domain.getSymbol());
    assertEquals(UnitType.WEIGHT, domain.getType());
    assertEquals("Unit of mass", domain.getDescription());
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
    var domain = UnitDomain.builder().id("KG").name("Kilogram").build();

    UnitEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals("KG", entity.getId());
    assertEquals("Kilogram", entity.getName());
    assertNull(entity.getSymbol());
    assertNull(entity.getType());
    assertNull(entity.getDescription());
    assertNull(entity.getStatus());
  }

  @Test
  @DisplayName("Should handle null fields in entity")
  void shouldHandleNullFieldsInEntity() {
    var entity = new UnitEntity();
    entity.setId("KG");
    entity.setName("Kilogram");

    UnitDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals("KG", domain.getId());
    assertEquals("Kilogram", domain.getName());
    assertNull(domain.getSymbol());
    assertNull(domain.getType());
    assertNull(domain.getDescription());
    assertNull(domain.getStatus());
  }
}
