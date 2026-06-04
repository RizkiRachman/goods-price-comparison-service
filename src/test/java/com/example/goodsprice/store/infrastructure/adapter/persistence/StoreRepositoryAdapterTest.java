package com.example.goodsprice.store.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.store.application.domain.model.StoreDomain;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreRepositoryAdapterTest {

  @Mock private JpaStoreRepository jpaRepository;
  @Mock private StoreMapper mapper;

  private StoreRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new StoreRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  @DisplayName("Should save store")
  void shouldSaveStore() {
    var domain = StoreDomain.builder().name("Toko Segar").build();
    var entity =
        new com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity();
    entity.setName("Toko Segar");

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepository.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.save(domain);

    assertNotNull(result);
    assertEquals("Toko Segar", result.getName());
    verify(jpaRepository).save(entity);
  }

  @Test
  @DisplayName("Should find store by id")
  void shouldFindById() {
    var entity =
        new com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity();
    entity.setId(1L);
    entity.setName("Toko Segar");
    var domain = StoreDomain.builder().id(1L).name("Toko Segar").build();

    when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Should return null when store not found")
  void shouldReturnNullWhenNotFound() {
    when(jpaRepository.findById(999L)).thenReturn(Optional.empty());

    assertNull(adapter.findById(999L));
  }

  @Test
  @DisplayName("Should return null when id is null")
  void shouldReturnNullWhenIdIsNull() {
    assertNull(adapter.findById(null));
  }

  @Test
  @DisplayName("Should find stores by name")
  void shouldFindByName() {
    var entity =
        new com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity();
    entity.setName("Toko Segar");
    var domain = StoreDomain.builder().name("Toko Segar").build();

    when(jpaRepository.findByName("Toko Segar")).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findByName("Toko Segar");

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should return empty list when name not found")
  void shouldReturnEmptyListWhenNameNotFound() {
    when(jpaRepository.findByName("NonExistent")).thenReturn(List.of());

    assertTrue(adapter.findByName("NonExistent").isEmpty());
  }

  @Test
  @DisplayName("Should find store by name and location")
  void shouldFindByNameAndLocation() {
    var entity =
        new com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity();
    entity.setName("Toko Segar");
    entity.setLocation("Jakarta");
    var domain = StoreDomain.builder().name("Toko Segar").location("Jakarta").build();

    when(jpaRepository.findByNameAndLocation("Toko Segar", "Jakarta"))
        .thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findByNameAndLocation("Toko Segar", "Jakarta");

    assertNotNull(result);
    assertEquals("Jakarta", result.getLocation());
  }

  @Test
  @DisplayName("Should return null when name and location not found")
  void shouldReturnNullWhenNameAndLocationNotFound() {
    when(jpaRepository.findByNameAndLocation("NonExistent", "Nowhere"))
        .thenReturn(Optional.empty());

    assertNull(adapter.findByNameAndLocation("NonExistent", "Nowhere"));
  }

  @Test
  @DisplayName("Should check existence by name and location")
  void shouldCheckExistsByNameAndLocation() {
    when(jpaRepository.existsByNameAndLocation("Toko Segar", "Jakarta")).thenReturn(true);

    assertTrue(adapter.existsByNameAndLocation("Toko Segar", "Jakarta"));
  }

  @Test
  @DisplayName("Should find all stores by ids")
  void shouldFindAllByIds() {
    var entity =
        new com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity();
    entity.setId(1L);
    var domain = StoreDomain.builder().id(1L).build();

    when(jpaRepository.findAllById(List.of(1L))).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findAllById(List.of(1L));

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should delete store by id")
  void shouldDeleteById() {
    adapter.deleteById(1L);

    verify(jpaRepository).deleteById(1L);
  }
}
