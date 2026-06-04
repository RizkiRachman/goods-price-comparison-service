package com.example.goodsprice.store.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StoreMapperTest {

  private StoreMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new StoreMapperImpl();
  }

  @Test
  @DisplayName("Should map domain to entity")
  void shouldMapDomainToEntity() {
    var domain =
        StoreDomain.builder()
            .id(1L)
            .name("Toko Segar")
            .location("Jakarta")
            .chain("Segar Group")
            .address("Jl. Sudirman No. 1")
            .latitude(-6.2)
            .longitude(106.8)
            .status("ACTIVE")
            .build();

    StoreEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals(1L, entity.getId());
    assertEquals("Toko Segar", entity.getName());
    assertEquals("Jakarta", entity.getLocation());
    assertEquals("Segar Group", entity.getChain());
    assertEquals("Jl. Sudirman No. 1", entity.getAddress());
    assertEquals(-6.2, entity.getLatitude(), 0.001);
    assertEquals(106.8, entity.getLongitude(), 0.001);
    assertEquals("ACTIVE", entity.getStatus());
  }

  @Test
  @DisplayName("Should map entity to domain")
  void shouldMapEntityToDomain() {
    var entity = new StoreEntity();
    entity.setId(1L);
    entity.setName("Toko Segar");
    entity.setLocation("Jakarta");
    entity.setChain("Segar Group");
    entity.setAddress("Jl. Sudirman No. 1");
    entity.setLatitude(-6.2);
    entity.setLongitude(106.8);
    entity.setStatus("ACTIVE");

    StoreDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals(1L, domain.getId());
    assertEquals("Toko Segar", domain.getName());
    assertEquals("Jakarta", domain.getLocation());
    assertEquals("Segar Group", domain.getChain());
    assertEquals("Jl. Sudirman No. 1", domain.getAddress());
    assertEquals(-6.2, domain.getLatitude(), 0.001);
    assertEquals(106.8, domain.getLongitude(), 0.001);
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
    var domain = StoreDomain.builder().name("Toko Segar").build();

    StoreEntity entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals("Toko Segar", entity.getName());
    assertNull(entity.getId());
    assertNull(entity.getLocation());
    assertNull(entity.getChain());
    assertNull(entity.getAddress());
    assertNull(entity.getLatitude());
    assertNull(entity.getLongitude());
    assertNull(entity.getStatus());
  }

  @Test
  @DisplayName("Should handle null fields in entity")
  void shouldHandleNullFieldsInEntity() {
    var entity = new StoreEntity();
    entity.setName("Toko Segar");

    StoreDomain domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals("Toko Segar", domain.getName());
    assertNull(domain.getId());
    assertNull(domain.getLocation());
    assertNull(domain.getChain());
    assertNull(domain.getAddress());
    assertNull(domain.getLatitude());
    assertNull(domain.getLongitude());
    assertNull(domain.getStatus());
  }
}
