package com.example.goodsprice.store.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.dto.StoreCriteria;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
    var entity = new StoreEntity();
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
    var entity = new StoreEntity();
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
    var entity = new StoreEntity();
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
    var entity = new StoreEntity();
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
    var entity = new StoreEntity();
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

  @Test
  @DisplayName("Should return false when store does not exist by name and location")
  void shouldReturnFalseWhenStoreDoesNotExist() {
    when(jpaRepository.existsByNameAndLocation("NonExistent", "Nowhere")).thenReturn(false);

    var result = adapter.existsByNameAndLocation("NonExistent", "Nowhere");

    assertFalse(result);
  }

  @Test
  @DisplayName("Should return empty list when findAllById receives empty ids")
  void shouldReturnEmptyListWhenFindAllByIdWithEmptyIds() {
    when(jpaRepository.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());

    var result = adapter.findAllById(Collections.emptyList());

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should find all stores with no filters")
  void shouldFindAllWithNoFilters() {
    var pageRequest = new PageRequestDto(1, 10, "name", "asc");
    var criteria = new StoreCriteria(pageRequest, null, null, null, null);
    var entity = new StoreEntity();
    entity.setName("Toko Segar");
    var domain = StoreDomain.builder().name("Toko Segar").build();

    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(entity)));
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<StoreDomain> result = adapter.findAll(criteria);

    assertEquals(1, result.content().size());
    assertEquals("Toko Segar", result.content().get(0).getName());
    assertEquals(1, result.totalPages());
  }

  @Test
  @DisplayName("Should find all stores with search filter")
  void shouldFindAllWithSearchFilter() {
    var pageRequest = new PageRequestDto(1, 5, "name", "asc");
    var criteria = new StoreCriteria(pageRequest, "toko", null, null, null);
    var entity = new StoreEntity();
    entity.setName("Toko Makmur");
    var domain = StoreDomain.builder().name("Toko Makmur").build();

    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(entity)));
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<StoreDomain> result = adapter.findAll(criteria);

    assertEquals(1, result.content().size());
    assertEquals("Toko Makmur", result.content().get(0).getName());
  }

  @Test
  @DisplayName("Should find all stores with status filter")
  void shouldFindAllWithStatusFilter() {
    var pageRequest = new PageRequestDto(1, 20, "id", "desc");
    var criteria = new StoreCriteria(pageRequest, null, "ACTIVE", null, null);
    var entity = new StoreEntity();
    entity.setName("Toko Aktif");
    entity.setStatus("ACTIVE");
    var domain = StoreDomain.builder().name("Toko Aktif").status("ACTIVE").build();

    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(entity)));
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<StoreDomain> result = adapter.findAll(criteria);

    assertEquals(1, result.content().size());
    assertEquals("ACTIVE", result.content().get(0).getStatus());
  }

  @Test
  @DisplayName("Should find all stores with chain filter")
  void shouldFindAllWithChainFilter() {
    var pageRequest = new PageRequestDto(1, 10, "name", "asc");
    var criteria = new StoreCriteria(pageRequest, null, null, "Segar", null);
    var entity = new StoreEntity();
    entity.setName("Toko Segar");
    entity.setChain("Segar Group");
    var domain = StoreDomain.builder().name("Toko Segar").chain("Segar Group").build();

    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(entity)));
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<StoreDomain> result = adapter.findAll(criteria);

    assertEquals(1, result.content().size());
    assertEquals("Segar Group", result.content().get(0).getChain());
  }

  @Test
  @DisplayName("Should find all stores with location filter")
  void shouldFindAllWithLocationFilter() {
    var pageRequest = new PageRequestDto(1, 10, "name", "asc");
    var criteria = new StoreCriteria(pageRequest, null, null, null, "Jakarta");
    var entity = new StoreEntity();
    entity.setName("Toko Jakarta");
    entity.setLocation("Jakarta");
    var domain = StoreDomain.builder().name("Toko Jakarta").location("Jakarta").build();

    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(entity)));
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<StoreDomain> result = adapter.findAll(criteria);

    assertEquals(1, result.content().size());
    assertEquals("Jakarta", result.content().get(0).getLocation());
  }

  @Test
  @DisplayName("Should find all stores with all filters")
  void shouldFindAllWithAllFilters() {
    var pageRequest = new PageRequestDto(1, 10, "name", "asc");
    var criteria = new StoreCriteria(pageRequest, "toko", "ACTIVE", "Segar", "Jakarta");
    var entity = new StoreEntity();
    entity.setName("Toko Segar Jakarta");
    entity.setStatus("ACTIVE");
    entity.setChain("Segar Group");
    entity.setLocation("Jakarta");
    var domain =
        StoreDomain.builder()
            .name("Toko Segar Jakarta")
            .status("ACTIVE")
            .chain("Segar Group")
            .location("Jakarta")
            .build();

    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(entity)));
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<StoreDomain> result = adapter.findAll(criteria);

    assertEquals(1, result.content().size());
    assertEquals("Toko Segar Jakarta", result.content().get(0).getName());
    assertEquals("ACTIVE", result.content().get(0).getStatus());
  }

  @Test
  @DisplayName("Should return empty page when no stores match criteria")
  void shouldReturnEmptyPageWhenNoStoresFound() {
    var pageRequest = new PageRequestDto(1, 10, "name", "asc");
    var criteria = new StoreCriteria(pageRequest, "NonExistent", null, null, null);

    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    PageResponse<StoreDomain> result = adapter.findAll(criteria);

    assertTrue(result.content().isEmpty());
    assertEquals(0, result.totalElements());
  }
}
