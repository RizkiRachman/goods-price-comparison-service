package com.example.goodsprice.price.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.dto.PriceCriteria;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import java.time.LocalDate;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class PriceRepositoryAdapterTest {

  @Mock private JpaPriceRepository jpaRepo;
  @Mock private PriceMapper mapper;

  private PriceRepositoryAdapter adapter;

  private PriceDomain domain;
  private PriceEntity entity;

  @BeforeEach
  void setUp() {
    adapter = new PriceRepositoryAdapter(jpaRepo, mapper);
    domain =
        PriceDomain.builder()
            .id(1L)
            .productId(100L)
            .storeId(10L)
            .price(15000.0)
            .unitPrice(15000.0)
            .dateRecorded(LocalDate.of(2026, 6, 1))
            .isPromo(false)
            .build();
    entity = new PriceEntity();
    entity.setId(1L);
    entity.setProductId(100L);
    entity.setStoreId(10L);
    entity.setPrice(15000.0);
    entity.setDateRecorded(LocalDate.of(2026, 6, 1));
  }

  @Test
  @DisplayName("Should save a price domain")
  void shouldSavePrice() {
    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepo.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PriceDomain result = adapter.save(domain);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(jpaRepo).save(entity);
  }

  @Test
  @DisplayName("Should find by id")
  void shouldFindById() {
    when(jpaRepo.findById(1L)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    PriceDomain result = adapter.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Should return null when finding non-existent id")
  void shouldReturnNullWhenNotFound() {
    when(jpaRepo.findById(999L)).thenReturn(Optional.empty());

    assertNull(adapter.findById(999L));
  }

  @Test
  @DisplayName("Should save all prices")
  void shouldSaveAll() {
    PriceDomain domain2 =
        PriceDomain.builder()
            .id(2L)
            .productId(200L)
            .storeId(20L)
            .price(10000.0)
            .dateRecorded(LocalDate.now())
            .isPromo(false)
            .build();
    PriceEntity entity2 = new PriceEntity();
    entity2.setId(2L);

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(mapper.toEntity(domain2)).thenReturn(entity2);
    when(jpaRepo.saveAll(anyList())).thenReturn(List.of(entity, entity2));
    when(mapper.toDomain(entity)).thenReturn(domain);
    when(mapper.toDomain(entity2)).thenReturn(domain2);

    List<PriceDomain> result = adapter.saveAll(List.of(domain, domain2));

    assertEquals(2, result.size());
    verify(jpaRepo).saveAll(anyList());
  }

  @Test
  @DisplayName("Should find by product id")
  void shouldFindByProductId() {
    when(jpaRepo.findByProductId(100L)).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    List<PriceDomain> result = adapter.findByProductId(100L);

    assertEquals(1, result.size());
    assertEquals(100L, result.getFirst().getProductId());
  }

  @Test
  @DisplayName("Should find by product id and date range")
  void shouldFindByProductIdAndDateRange() {
    LocalDate start = LocalDate.of(2026, 1, 1);
    LocalDate end = LocalDate.of(2026, 12, 31);
    when(jpaRepo.findByProductIdAndDateRange(100L, start, end)).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    List<PriceDomain> result = adapter.findByProductIdAndDateRange(100L, start, end);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should find cheapest by product id")
  void shouldFindCheapestByProductId() {
    when(jpaRepo.findCheapestByProductId(100L)).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    List<PriceDomain> result = adapter.findCheapestByProductId(100L);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should find cheapest by product ids")
  void shouldFindCheapestByProductIds() {
    when(jpaRepo.findCheapestByProductIds(List.of(100L, 200L))).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    List<PriceDomain> result = adapter.findCheapestByProductIds(List.of(100L, 200L));

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should find all by product ids")
  void shouldFindAllByProductIds() {
    when(jpaRepo.findAllByProductIds(List.of(100L))).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    List<PriceDomain> result = adapter.findAllByProductIds(List.of(100L));

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should find product ids by store ids")
  void shouldFindProductIdsByStoreIds() {
    when(jpaRepo.findDistinctProductIdsByStoreIds(List.of(10L))).thenReturn(List.of(100L, 200L));

    List<Long> result = adapter.findProductIdsByStoreIds(List.of(10L));

    assertEquals(List.of(100L, 200L), result);
  }

  @Test
  @DisplayName("Should find by product id with filters")
  void shouldFindByProductIdWithFilters() {
    var pageRequest = new PageRequestDto(1, 20, "dateRecorded", "desc");
    var criteria = new PriceCriteria(100L, null, null, null, null, pageRequest);

    Sort sort = Sort.by(Sort.Direction.DESC, "dateRecorded");
    Pageable springPageable = PageRequest.of(0, 20, sort);
    Page<PriceEntity> page = new PageImpl<>(List.of(entity));

    when(jpaRepo.findByProductIdWithFilters(eq(100L), any(), any(), any(), any(), any()))
        .thenReturn(page);
    when(mapper.toDomain(entity)).thenReturn(domain);

    PageResponse<PriceDomain> result = adapter.findByProductIdWithFilters(criteria);

    assertEquals(1, result.content().size());
    assertEquals(1, result.totalElements());
  }

  @Test
  @DisplayName("Should delete by id")
  void shouldDeleteById() {
    adapter.deleteById(1L);

    verify(jpaRepo).deleteById(1L);
  }

  @Test
  @DisplayName("Should check exists by id")
  void shouldExistsById() {
    when(jpaRepo.existsById(1L)).thenReturn(true);

    assertTrue(adapter.existsById(1L));
  }

  @Test
  @DisplayName("Should find all with warning")
  void shouldFindAll() {
    when(jpaRepo.findAll()).thenReturn(List.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    List<PriceDomain> result = adapter.findAll();

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should return empty list when no prices found by product id")
  void shouldReturnEmptyWhenNoPricesByProduct() {
    when(jpaRepo.findByProductId(999L)).thenReturn(List.of());

    List<PriceDomain> result = adapter.findByProductId(999L);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should return empty list when no cheapest prices found")
  void shouldReturnEmptyWhenNoCheapestPrices() {
    when(jpaRepo.findCheapestByProductId(999L)).thenReturn(List.of());

    List<PriceDomain> result = adapter.findCheapestByProductId(999L);

    assertTrue(result.isEmpty());
  }
}
