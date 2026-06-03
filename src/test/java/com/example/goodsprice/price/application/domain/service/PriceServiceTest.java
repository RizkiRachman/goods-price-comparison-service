package com.example.goodsprice.price.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.price.application.domain.model.PriceCreateItem;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.dto.PriceCriteria;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

  @Mock private PriceRepositoryPort priceRepository;
  @Mock private ProductInPort productInPort;

  @InjectMocks private PriceService priceService;

  private PriceDomain price1;
  private PriceDomain price2;

  @BeforeEach
  void setUp() {
    price1 = PriceDomain.builder()
        .id(1L).productId(100L).storeId(10L)
        .price(15000.0).unitPrice(15000.0)
        .dateRecorded(LocalDate.of(2026, 6, 1))
        .isPromo(false).build();
    price2 = PriceDomain.builder()
        .id(2L).productId(100L).storeId(20L)
        .price(12000.0).unitPrice(12000.0)
        .dateRecorded(LocalDate.of(2026, 6, 2))
        .isPromo(true).build();
  }

  @Test
  @DisplayName("Should create a price record")
  void shouldCreatePrice() {
    when(priceRepository.save(any(PriceDomain.class))).thenReturn(price1);

    var result = priceService.create(100L, 10L, 15000.0, 15000.0, LocalDate.of(2026, 6, 1), false);

    assertNotNull(result);
    assertEquals(100L, result.getProductId());
    assertEquals(15000.0, result.getPrice());
    verify(priceRepository).save(any(PriceDomain.class));
    verify(productInPort).updateLastPriceUpdate(eq(100L), any());
  }

  @Test
  @DisplayName("Should create price with null optional fields")
  void shouldCreatePriceWithNullOptionals() {
    when(priceRepository.save(any(PriceDomain.class))).thenReturn(price1);

    var result = priceService.create(100L, 10L, 15000.0, null, null, null);

    assertNotNull(result);
    verify(priceRepository).save(any(PriceDomain.class));
  }

  @Test
  @DisplayName("Should find price by id")
  void shouldFindPriceById() {
    when(priceRepository.findById(1L)).thenReturn(price1);

    var result = priceService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(15000.0, result.getPrice());
  }

  @Test
  @DisplayName("Should throw NotFoundException when price not found by id")
  void shouldThrowExceptionWhenPriceNotFound() {
    when(priceRepository.findById(999L)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> priceService.findById(999L));
  }

  @Test
  @DisplayName("Should search by product with date range")
  void shouldSearchByProductWithDateRange() {
    when(priceRepository.findByProductIdAndDateRange(eq(100L), any(), any()))
        .thenReturn(List.of(price1, price2));

    var result = priceService.searchByProduct(100L, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

    assertEquals(2, result.size());
    verify(priceRepository).findByProductIdAndDateRange(eq(100L), any(), any());
  }

  @Test
  @DisplayName("Should search by product without date range")
  void shouldSearchByProductWithoutDateRange() {
    when(priceRepository.findByProductId(100L)).thenReturn(List.of(price1));

    assertAllDateRangeFallbacks();
  }

  @Test
  @DisplayName("Should search by product with null start date")
  void shouldSearchByProductWithNullStartDate() {
    when(priceRepository.findByProductId(100L)).thenReturn(List.of(price1));

    var result = priceService.searchByProduct(100L, null, LocalDate.of(2026, 12, 31));

    assertEquals(1, result.size());
    verify(priceRepository).findByProductId(100L);
  }

  @Test
  @DisplayName("Should search by product with null end date")
  void shouldSearchByProductWithNullEndDate() {
    when(priceRepository.findByProductId(100L)).thenReturn(List.of(price1));

    var result = priceService.searchByProduct(100L, LocalDate.of(2026, 1, 1), null);

    assertEquals(1, result.size());
    verify(priceRepository).findByProductId(100L);
  }

  @Test
  @DisplayName("Should return empty list when no prices found")
  void shouldReturnEmptyListWhenNoPrices() {
    when(priceRepository.findByProductId(999L)).thenReturn(List.of());

    var result = priceService.searchByProduct(999L, null, null);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should search prices by criteria")
  void shouldSearchByCriteria() {
    var criteria = new PriceCriteria(100L, null, null, null, null, new PageRequestDto(0, 20, "dateRecorded", "desc"));
    var pageResponse = PageResponse.of(List.of(price1, price2), 0, 20, 2);
    when(priceRepository.findByProductIdWithFilters(criteria)).thenReturn(pageResponse);

    var result = priceService.searchByProduct(criteria);

    assertEquals(2, result.content().size());
    assertEquals(2, result.totalElements());
  }

  @Test
  @DisplayName("Should find cheapest price by product")
  void shouldFindCheapestByProduct() {
    when(priceRepository.findCheapestByProductId(100L)).thenReturn(List.of(price2));

    var result = priceService.findCheapestByProduct(100L);

    assertNotNull(result);
    assertEquals(12000.0, result.getPrice());
  }

  @Test
  @DisplayName("Should return null when no cheapest price found")
  void shouldReturnNullWhenNoCheapestPrice() {
    when(priceRepository.findCheapestByProductId(999L)).thenReturn(List.of());

    var result = priceService.findCheapestByProduct(999L);

    assertNull(result);
  }

  @Test
  @DisplayName("Should find cheapest by multiple products")
  void shouldFindCheapestByProducts() {
    var priceA = PriceDomain.builder().id(1L).productId(100L).price(15000.0).build();
    var priceB = PriceDomain.builder().id(2L).productId(200L).price(12000.0).build();
    when(priceRepository.findCheapestByProductIds(List.of(100L, 200L))).thenReturn(List.of(priceA, priceB));

    var result = priceService.findCheapestByProducts(List.of(100L, 200L));

    assertEquals(2, result.size());
    assertEquals(15000.0, result.get(100L).getPrice());
  }

  @Test
  @DisplayName("Should return empty map for empty product id list")
  void shouldReturnEmptyMapForEmptyProductIds() {
    when(priceRepository.findCheapestByProductIds(List.of())).thenReturn(List.of());

    var result = priceService.findCheapestByProducts(List.of());

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should find all prices by product ids")
  void shouldFindAllByProductIds() {
    when(priceRepository.findAllByProductIds(List.of(100L))).thenReturn(List.of(price1, price2));

    var result = priceService.findAllByProductIds(List.of(100L));

    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("Should handle createBatch with null items")
  void shouldReturnEarlyWhenBatchItemsNull() {
    priceService.createBatch(null);
    verify(priceRepository, never()).saveAll(any());
  }

  @Test
  @DisplayName("Should handle createBatch with empty items")
  void shouldReturnEarlyWhenBatchItemsEmpty() {
    priceService.createBatch(List.of());
    verify(priceRepository, never()).saveAll(any());
  }

  @Test
  @DisplayName("Should filter invalid items in createBatch")
  void shouldFilterInvalidItemsInBatch() {
    var valid = new PriceCreateItem(100L, 10L, 15000.0, 15000.0, LocalDate.of(2026, 6, 1), false);
    var invalid = new PriceCreateItem(null, 10L, 5000.0, 5000.0, LocalDate.of(2026, 6, 1), false);
    when(priceRepository.saveAll(any())).thenReturn(List.of(price1));

    priceService.createBatch(List.of(valid, invalid));

    verify(priceRepository).saveAll(any());
    verify(productInPort).updateLastPriceUpdate(eq(100L), any());
  }

  @Test
  @DisplayName("Should delete price by id")
  void shouldDeletePriceById() {
    when(priceRepository.findById(1L)).thenReturn(price1);

    priceService.deleteById(1L);

    verify(priceRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Should throw NotFoundException when deleting non-existent")
  void shouldThrowWhenDeletingNonExistent() {
    when(priceRepository.findById(999L)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> priceService.deleteById(999L));
    verify(priceRepository, never()).deleteById(999L);
  }

  @Test
  @DisplayName("Should update price fields")
  void shouldUpdatePrice() {
    when(priceRepository.findById(1L)).thenReturn(price1);
    when(priceRepository.save(any(PriceDomain.class))).thenReturn(price1);

    var result = priceService.update(1L, 18000.0, 18000.0, LocalDate.of(2026, 6, 5), true);

    assertNotNull(result);
    verify(priceRepository).save(any(PriceDomain.class));
    verify(productInPort).updateLastPriceUpdate(eq(100L), any());
  }

  @Test
  @DisplayName("Should keep existing values when update fields are null")
  void shouldKeepExistingOnNullUpdate() {
    when(priceRepository.findById(1L)).thenReturn(price1);
    when(priceRepository.save(any(PriceDomain.class))).thenReturn(price1);

    var result = priceService.update(1L, null, null, null, null);

    assertNotNull(result);
    assertEquals(15000.0, result.getPrice());
    assertEquals(15000.0, result.getUnitPrice());
    verify(priceRepository).save(any(PriceDomain.class));
  }

  @Test
  @DisplayName("Should throw NotFoundException when updating non-existent")
  void shouldThrowWhenUpdatingNonExistent() {
    when(priceRepository.findById(999L)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> priceService.update(999L, 100.0, null, null, null));
  }

  private void assertAllDateRangeFallbacks() {
    // Both null
    var result = priceService.searchByProduct(100L, null, null);
    assertEquals(1, result.size());
    verify(priceRepository).findByProductId(100L);
  }
}
