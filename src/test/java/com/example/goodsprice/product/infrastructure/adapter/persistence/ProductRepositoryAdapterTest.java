package com.example.goodsprice.product.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

  @Mock private JpaProductRepository jpaRepository;
  @Mock private ProductMapper mapper;

  private ProductRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new ProductRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  @DisplayName("Should save product")
  void shouldSaveProduct() {
    var domain = ProductDomain.builder().name("Susu Kotak").category("Minuman").build();
    var entity = new ProductEntity();
    entity.setName("Susu Kotak");

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepository.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.save(domain);

    assertNotNull(result);
    assertEquals("Susu Kotak", result.getName());
    verify(jpaRepository).save(entity);
  }

  @Test
  @DisplayName("Should find product by id")
  void shouldFindById() {
    var entity = new ProductEntity();
    entity.setId(1L);
    entity.setName("Susu Kotak");
    var domain = ProductDomain.builder().id(1L).name("Susu Kotak").build();

    when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Should return null when product not found")
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
  @DisplayName("Should find product by name")
  void shouldFindByName() {
    var entity = new ProductEntity();
    entity.setName("Susu Kotak");
    var domain = ProductDomain.builder().name("Susu Kotak").build();

    when(jpaRepository.findByName("Susu Kotak")).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findByName("Susu Kotak");

    assertNotNull(result);
    assertEquals("Susu Kotak", result.getName());
  }

  @Test
  @DisplayName("Should return null when product name not found")
  void shouldReturnNullWhenNameNotFound() {
    when(jpaRepository.findByName("NonExistent")).thenReturn(Optional.empty());

    assertNull(adapter.findByName("NonExistent"));
  }

  @Test
  @DisplayName("Should search products by name containing")
  void shouldSearchByName() {
    var entity1 = new ProductEntity();
    entity1.setName("Susu Kotak");
    var entity2 = new ProductEntity();
    entity2.setName("Susu Bubuk");
    var domain1 = ProductDomain.builder().name("Susu Kotak").build();
    var domain2 = ProductDomain.builder().name("Susu Bubuk").build();

    when(jpaRepository.findByNameContainingIgnoreCase("susu"))
        .thenReturn(List.of(entity1, entity2));
    when(mapper.toDomain(entity1)).thenReturn(domain1);
    when(mapper.toDomain(entity2)).thenReturn(domain2);

    var result = adapter.searchByName("susu");

    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("Should find all products by names list")
  void shouldFindAllByNames() {
    var entity = new ProductEntity();
    entity.setName("Susu Kotak");
    var domain = ProductDomain.builder().name("Susu Kotak").build();

    when(jpaRepository.findByNameIn(List.of("Susu Kotak"))).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findAllByNames(List.of("Susu Kotak"));

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should check existence by name")
  void shouldCheckExistsByName() {
    when(jpaRepository.existsByName("Susu Kotak")).thenReturn(true);

    assertTrue(adapter.existsByName("Susu Kotak"));
  }

  @Test
  @DisplayName("Should search with criteria")
  void shouldSearchWithCriteria() {
    var criteria = ProductSearchCriteria.builder().search("Susu").page(0).size(20).build();
    var productPage = Page.empty();

    when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);

    var result = adapter.search(criteria);

    assertNotNull(result);
  }

  @Test
  @DisplayName("Should delete product by id")
  void shouldDeleteById() {
    adapter.deleteById(1L);

    verify(jpaRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Should find products needing summary update")
  void shouldFindProductsNeedingSummaryUpdate() {
    var entity = new ProductEntity();
    entity.setId(1L);
    entity.setName("Susu Kotak");
    var domain = ProductDomain.builder().id(1L).name("Susu Kotak").build();

    when(jpaRepository.findProductsNeedingSummaryUpdate(10)).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findProductsNeedingSummaryUpdate(10);

    assertEquals(1, result.size());
    assertEquals(1L, result.getFirst().getId());
    verify(jpaRepository).findProductsNeedingSummaryUpdate(10);
  }

  @Test
  @DisplayName("Should update summaryLastCalculated for single product")
  void shouldUpdateSummaryLastCalculatedForSingleProduct() {
    var timestamp = LocalDateTime.of(2026, 6, 28, 10, 0);

    adapter.updateSummaryLastCalculated(1L, timestamp);

    verify(jpaRepository).updateSummaryLastCalculated(1L, timestamp);
  }

  @Test
  @DisplayName("Should update summaryLastCalculated for multiple products")
  void shouldUpdateSummaryLastCalculatedForMultipleProducts() {
    var timestamp = LocalDateTime.of(2026, 6, 28, 10, 0);
    var ids = List.of(1L, 2L, 3L);

    adapter.updateSummaryLastCalculated(ids, timestamp);

    verify(jpaRepository).updateSummaryLastCalculated(ids, timestamp);
  }

  @Test
  @DisplayName("Should not update summaryLastCalculated for empty list")
  void shouldNotUpdateSummaryLastCalculatedForEmptyList() {
    var timestamp = LocalDateTime.of(2026, 6, 28, 10, 0);

    adapter.updateSummaryLastCalculated(List.of(), timestamp);

    verify(jpaRepository, never()).updateSummaryLastCalculated(anyList(), any());
  }

  @Test
  @DisplayName("Should update lastPriceUpdate for single product")
  void shouldUpdateLastPriceUpdate() {
    var timestamp = LocalDateTime.of(2026, 6, 28, 10, 0);

    adapter.updateLastPriceUpdate(1L, timestamp);

    verify(jpaRepository).updateLastPriceUpdate(1L, timestamp);
  }
}
