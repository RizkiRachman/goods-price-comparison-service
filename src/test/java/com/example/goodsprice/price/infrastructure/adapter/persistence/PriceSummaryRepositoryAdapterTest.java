package com.example.goodsprice.price.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceSummaryEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceSummaryRepositoryAdapterTest {

  @Mock private JpaPriceSummaryRepository jpaRepository;
  @Mock private PriceSummaryMapper mapper;

  private PriceSummaryRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new PriceSummaryRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  @DisplayName("Should save price summary")
  void shouldSavePriceSummary() {
    ProductPriceSummary domain =
        ProductPriceSummary.builder()
            .productId(1L)
            .avgPrice(new BigDecimal("10.00"))
            .lastCalculatedAt(LocalDateTime.now())
            .build();

    PriceSummaryEntity entity = new PriceSummaryEntity();
    entity.setProductId(1L);

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepository.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    ProductPriceSummary result = adapter.save(domain);

    assertNotNull(result);
    assertEquals(1L, result.getProductId());
    verify(jpaRepository).save(entity);
  }

  @Test
  @DisplayName("Should return null when saving null")
  void shouldReturnNullWhenSavingNull() {
    assertNull(adapter.save(null));
    verify(jpaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should save all price summaries")
  void shouldSaveAllPriceSummaries() {
    ProductPriceSummary summary1 =
        ProductPriceSummary.builder()
            .productId(1L)
            .avgPrice(new BigDecimal("10.00"))
            .lastCalculatedAt(LocalDateTime.now())
            .build();
    ProductPriceSummary summary2 =
        ProductPriceSummary.builder()
            .productId(2L)
            .avgPrice(new BigDecimal("20.00"))
            .lastCalculatedAt(LocalDateTime.now())
            .build();

    PriceSummaryEntity entity1 = new PriceSummaryEntity();
    entity1.setProductId(1L);
    PriceSummaryEntity entity2 = new PriceSummaryEntity();
    entity2.setProductId(2L);

    when(mapper.toEntity(summary1)).thenReturn(entity1);
    when(mapper.toEntity(summary2)).thenReturn(entity2);
    when(jpaRepository.saveAll(any())).thenReturn(List.of(entity1, entity2));
    when(mapper.toDomain(entity1)).thenReturn(summary1);
    when(mapper.toDomain(entity2)).thenReturn(summary2);

    List<ProductPriceSummary> result = adapter.saveAll(List.of(summary1, summary2));

    assertEquals(2, result.size());
    verify(jpaRepository).saveAll(any());
  }

  @Test
  @DisplayName("Should return empty list when saving empty list")
  void shouldReturnEmptyListWhenSavingEmptyList() {
    assertTrue(adapter.saveAll(List.of()).isEmpty());
    verify(jpaRepository, never()).saveAll(any());
  }

  @Test
  @DisplayName("Should return empty list when saving null")
  void shouldReturnEmptyListWhenSavingNull() {
    assertTrue(adapter.saveAll(null).isEmpty());
    verify(jpaRepository, never()).saveAll(any());
  }

  @Test
  @DisplayName("Should find by product id")
  void shouldFindByProductId() {
    Long productId = 1L;
    PriceSummaryEntity entity = new PriceSummaryEntity();
    entity.setProductId(productId);

    ProductPriceSummary domain =
        ProductPriceSummary.builder()
            .productId(productId)
            .avgPrice(new BigDecimal("10.00"))
            .lastCalculatedAt(LocalDateTime.now())
            .build();

    when(jpaRepository.findById(productId)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    ProductPriceSummary result = adapter.findByProductId(productId);

    assertNotNull(result);
    assertEquals(productId, result.getProductId());
  }

  @Test
  @DisplayName("Should return null when product not found")
  void shouldReturnNullWhenProductNotFound() {
    when(jpaRepository.findById(1L)).thenReturn(Optional.empty());

    assertNull(adapter.findByProductId(1L));
  }

  @Test
  @DisplayName("Should return null when product id is null")
  void shouldReturnNullWhenProductIdIsNull() {
    assertNull(adapter.findByProductId(null));
    verify(jpaRepository, never()).findById(any());
  }

  @Test
  @DisplayName("Should find by product ids")
  void shouldFindByProductIds() {
    Set<Long> productIds = Set.of(1L, 2L);

    PriceSummaryEntity entity1 = new PriceSummaryEntity();
    entity1.setProductId(1L);
    PriceSummaryEntity entity2 = new PriceSummaryEntity();
    entity2.setProductId(2L);

    ProductPriceSummary summary1 =
        ProductPriceSummary.builder().productId(1L).lastCalculatedAt(LocalDateTime.now()).build();
    ProductPriceSummary summary2 =
        ProductPriceSummary.builder().productId(2L).lastCalculatedAt(LocalDateTime.now()).build();

    when(jpaRepository.findByProductIdIn(productIds)).thenReturn(List.of(entity1, entity2));
    when(mapper.toDomain(entity1)).thenReturn(summary1);
    when(mapper.toDomain(entity2)).thenReturn(summary2);

    List<ProductPriceSummary> result = adapter.findByProductIds(productIds);

    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("Should return empty list when product ids is empty")
  void shouldReturnEmptyListWhenProductIdsIsEmpty() {
    assertTrue(adapter.findByProductIds(Set.of()).isEmpty());
    verify(jpaRepository, never()).findByProductIdIn(any());
  }

  @Test
  @DisplayName("Should return empty list when product ids is null")
  void shouldReturnEmptyListWhenProductIdsIsNull() {
    assertTrue(adapter.findByProductIds(null).isEmpty());
    verify(jpaRepository, never()).findByProductIdIn(any());
  }

  @Test
  @DisplayName("Should map entity fields correctly")
  void shouldMapEntityFieldsCorrectly() {
    PriceSummaryEntity entity = new PriceSummaryEntity();
    entity.setProductId(1L);
    entity.setAvgPrice(new BigDecimal("15.50"));
    entity.setMinPrice(new BigDecimal("10.00"));
    entity.setMaxPrice(new BigDecimal("20.00"));
    entity.setStoreCount(3);
    entity.setPriceCount(10);
    entity.setLastCalculatedAt(LocalDateTime.now());
    entity.setLastPriceDate(LocalDate.now().minusDays(5));

    ProductPriceSummary domain =
        ProductPriceSummary.builder()
            .productId(1L)
            .avgPrice(new BigDecimal("15.50"))
            .minPrice(new BigDecimal("10.00"))
            .maxPrice(new BigDecimal("20.00"))
            .storeCount(3)
            .priceCount(10)
            .lastCalculatedAt(LocalDateTime.now())
            .lastPriceDate(LocalDate.now().minusDays(5))
            .build();

    when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    ProductPriceSummary result = adapter.findByProductId(1L);

    assertNotNull(result);
    assertEquals(new BigDecimal("15.50"), result.getAvgPrice());
    assertEquals(new BigDecimal("10.00"), result.getMinPrice());
    assertEquals(new BigDecimal("20.00"), result.getMaxPrice());
    assertEquals(3, result.getStoreCount());
    assertEquals(10, result.getPriceCount());
  }
}
