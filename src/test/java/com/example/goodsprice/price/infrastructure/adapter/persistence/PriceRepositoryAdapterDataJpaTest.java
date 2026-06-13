package com.example.goodsprice.price.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PriceRepositoryAdapterDataJpaTest {

  @Autowired private JpaPriceRepository repository;

  @PersistenceContext private EntityManager entityManager;

  private PriceEntity createPrice(Long productId, Long storeId, double price, LocalDate date) {
    return new PriceEntity(null, productId, storeId, price, price, date, false);
  }

  private PriceEntity createPromoPrice(Long productId, Long storeId, double price, LocalDate date) {
    return new PriceEntity(null, productId, storeId, price, price, date, true);
  }

  @Test
  @DisplayName("Should persist and retrieve price with all fields")
  void shouldPersistAndRetrievePrice() {
    var entity = createPrice(1L, 1L, 15000.0, LocalDate.of(2026, 1, 15));
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId());

    assertTrue(found.isPresent());
    var price = found.get();
    assertEquals(1L, price.getProductId());
    assertEquals(1L, price.getStoreId());
    assertEquals(15000.0, price.getPrice(), 0.001);
    assertEquals(LocalDate.of(2026, 1, 15), price.getDateRecorded());
    assertFalse(price.getIsPromo());
  }

  @Test
  @DisplayName("Should return empty for non-existent id")
  void shouldReturnEmptyForNonExistentId() {
    Optional<PriceEntity> found = repository.findById(99999L);
    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should find prices by product id")
  void shouldFindByProductId() {
    repository.saveAndFlush(createPrice(10L, 1L, 10000.0, LocalDate.of(2026, 1, 1)));
    repository.saveAndFlush(createPrice(10L, 2L, 12000.0, LocalDate.of(2026, 1, 2)));
    repository.saveAndFlush(createPrice(20L, 1L, 15000.0, LocalDate.of(2026, 1, 3)));
    entityManager.clear();

    List<PriceEntity> results = repository.findByProductId(10L);

    assertEquals(2, results.size());
    assertTrue(results.stream().allMatch(p -> p.getProductId().equals(10L)));
  }

  @Test
  @DisplayName("Should return empty list when product id not found")
  void shouldReturnEmptyListWhenProductIdNotFound() {
    repository.saveAndFlush(createPrice(1L, 1L, 10000.0, LocalDate.now()));
    entityManager.clear();

    List<PriceEntity> results = repository.findByProductId(999L);

    assertTrue(results.isEmpty());
  }

  @Test
  @DisplayName("Should find prices by product id and date range")
  void shouldFindByProductIdAndDateRange() {
    repository.saveAndFlush(createPrice(30L, 1L, 10000.0, LocalDate.of(2026, 1, 1)));
    repository.saveAndFlush(createPrice(30L, 1L, 11000.0, LocalDate.of(2026, 2, 1)));
    repository.saveAndFlush(createPrice(30L, 1L, 12000.0, LocalDate.of(2026, 3, 1)));
    entityManager.clear();

    List<PriceEntity> results =
        repository.findByProductIdAndDateRange(
            30L, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 2, 15));

    assertEquals(1, results.size());
    assertEquals(11000.0, results.get(0).getPrice(), 0.001);
  }

  @Test
  @DisplayName("Should return empty list when date range has no results")
  void shouldReturnEmptyListWhenDateRangeHasNoResults() {
    repository.saveAndFlush(createPrice(40L, 1L, 10000.0, LocalDate.of(2026, 1, 1)));
    entityManager.clear();

    List<PriceEntity> results =
        repository.findByProductIdAndDateRange(
            40L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1));

    assertTrue(results.isEmpty());
  }

  @Test
  @DisplayName("Should find cheapest price by product id")
  void shouldFindCheapestByProductId() {
    repository.saveAndFlush(createPrice(50L, 1L, 20000.0, LocalDate.of(2026, 1, 1)));
    repository.saveAndFlush(createPrice(50L, 2L, 15000.0, LocalDate.of(2026, 1, 2)));
    repository.saveAndFlush(createPrice(50L, 3L, 18000.0, LocalDate.of(2026, 1, 3)));
    entityManager.clear();

    List<PriceEntity> results = repository.findCheapestByProductId(50L);

    assertFalse(results.isEmpty());
    assertEquals(15000.0, results.get(0).getPrice(), 0.001);
  }

  @Test
  @DisplayName("Should find cheapest by product ids")
  void shouldFindCheapestByProductIds() {
    repository.saveAndFlush(createPrice(60L, 1L, 10000.0, LocalDate.of(2026, 1, 1)));
    repository.saveAndFlush(createPrice(60L, 2L, 9000.0, LocalDate.of(2026, 1, 2)));
    repository.saveAndFlush(createPrice(70L, 1L, 20000.0, LocalDate.of(2026, 1, 3)));
    repository.saveAndFlush(createPrice(70L, 2L, 18000.0, LocalDate.of(2026, 1, 4)));
    entityManager.clear();

    List<PriceEntity> results = repository.findCheapestByProductIds(List.of(60L, 70L));

    assertEquals(2, results.size());
    var cheapest60 =
        results.stream().filter(p -> p.getProductId().equals(60L)).findFirst().orElseThrow();
    var cheapest70 =
        results.stream().filter(p -> p.getProductId().equals(70L)).findFirst().orElseThrow();
    assertEquals(9000.0, cheapest60.getPrice(), 0.001);
    assertEquals(18000.0, cheapest70.getPrice(), 0.001);
  }

  @Test
  @DisplayName("Should find distinct product ids by store ids")
  void shouldFindDistinctProductIdsByStoreIds() {
    repository.saveAndFlush(createPrice(10L, 100L, 10000.0, LocalDate.of(2026, 1, 1)));
    repository.saveAndFlush(createPrice(20L, 100L, 15000.0, LocalDate.of(2026, 1, 2)));
    repository.saveAndFlush(createPrice(30L, 200L, 20000.0, LocalDate.of(2026, 1, 3)));
    entityManager.clear();

    List<Long> productIds = repository.findDistinctProductIdsByStoreIds(List.of(100L));

    assertEquals(2, productIds.size());
    assertTrue(productIds.contains(10L));
    assertTrue(productIds.contains(20L));
    assertFalse(productIds.contains(30L));
  }

  @Test
  @DisplayName("Should return empty list for unknown store ids")
  void shouldReturnEmptyListForUnknownStoreIds() {
    List<Long> productIds = repository.findDistinctProductIdsByStoreIds(List.of(999L));
    assertTrue(productIds.isEmpty());
  }

  @Test
  @DisplayName("Should find all prices by product ids")
  void shouldFindAllByProductIds() {
    repository.saveAndFlush(createPrice(10L, 1L, 10000.0, LocalDate.of(2026, 1, 1)));
    repository.saveAndFlush(createPrice(20L, 1L, 15000.0, LocalDate.of(2026, 1, 2)));
    repository.saveAndFlush(createPrice(30L, 1L, 20000.0, LocalDate.of(2026, 1, 3)));
    entityManager.clear();

    List<PriceEntity> results = repository.findAllByProductIds(List.of(10L, 20L));

    assertEquals(2, results.size());
    assertTrue(results.stream().anyMatch(p -> p.getProductId().equals(10L)));
    assertTrue(results.stream().anyMatch(p -> p.getProductId().equals(20L)));
  }

  @Test
  @DisplayName("Should find by product id with filters")
  void shouldFindByProductIdWithFilters() {
    repository.saveAndFlush(createPrice(80L, 1L, 10000.0, LocalDate.of(2026, 1, 1)));
    repository.saveAndFlush(createPrice(80L, 1L, 11000.0, LocalDate.of(2026, 2, 1)));
    repository.saveAndFlush(createPromoPrice(80L, 2L, 9000.0, LocalDate.of(2026, 1, 15)));
    entityManager.clear();

    var pageable = PageRequest.of(0, 10);
    var page =
        repository.findByProductIdWithFilters(
            80L, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, null, pageable);

    assertEquals(2, page.getTotalElements());
    assertTrue(page.getContent().stream().allMatch(p -> p.getProductId().equals(80L)));
  }

  @Test
  @DisplayName("Should find by product id with promo filter")
  void shouldFindByProductIdWithPromoFilter() {
    repository.saveAndFlush(createPrice(90L, 1L, 10000.0, LocalDate.of(2026, 1, 1)));
    repository.saveAndFlush(createPromoPrice(90L, 2L, 8000.0, LocalDate.of(2026, 1, 15)));
    entityManager.clear();

    var pageable = PageRequest.of(0, 10);
    var page = repository.findByProductIdWithFilters(90L, null, null, null, true, pageable);

    assertEquals(1, page.getTotalElements());
    assertTrue(page.getContent().get(0).getIsPromo());
  }

  @Test
  @DisplayName("Should persist timestamps")
  void shouldPersistTimestamps() {
    var entity = createPrice(99L, 1L, 5000.0, LocalDate.now());
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId()).orElseThrow();

    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
  }
}
