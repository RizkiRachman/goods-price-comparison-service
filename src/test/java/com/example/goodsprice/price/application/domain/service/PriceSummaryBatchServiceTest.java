package com.example.goodsprice.price.application.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.price.application.port.out.PriceSummaryRepositoryPort;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PriceSummaryBatchServiceTest {

  @Mock private PriceRepositoryPort priceRepository;
  @Mock private PriceSummaryRepositoryPort priceSummaryRepository;
  @Mock private ProductRepositoryPort productRepository;

  private PriceSummaryBatchService batchService;

  @BeforeEach
  void setUp() {
    batchService =
        new PriceSummaryBatchService(priceRepository, priceSummaryRepository, productRepository);
    ReflectionTestUtils.setField(batchService, "batchSize", 100);
  }

  @Test
  @DisplayName("Should update summaries for products with new prices")
  void shouldUpdateSummariesForProductsWithNewPrices() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Test Product").build();
    when(productRepository.findProductsNeedingSummaryUpdate(anyInt()))
        .thenReturn(List.of(product))
        .thenReturn(List.of());

    LocalDate today = LocalDate.now();
    PriceDomain price1 =
        PriceDomain.builder()
            .id(1L)
            .productId(1L)
            .storeId(1L)
            .price(10.0)
            .dateRecorded(today)
            .build();
    PriceDomain price2 =
        PriceDomain.builder()
            .id(2L)
            .productId(1L)
            .storeId(2L)
            .price(15.0)
            .dateRecorded(today.minusDays(30))
            .build();
    when(priceRepository.findByProductId(1L)).thenReturn(List.of(price1, price2));

    batchService.updateSummaries();

    ArgumentCaptor<List<ProductPriceSummary>> summaryCaptor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository, times(1)).saveAll(summaryCaptor.capture());

    ProductPriceSummary summary = summaryCaptor.getValue().get(0);
    assertEquals(1L, summary.getProductId());
    assertEquals(new BigDecimal("12.50"), summary.getAvgPrice());
    assertEquals(new BigDecimal("10.00"), summary.getMinPrice());
    assertEquals(new BigDecimal("15.00"), summary.getMaxPrice());
    assertEquals(2, summary.getPriceCount());

    verify(productRepository).updateSummaryLastCalculated(eq(1L), any(LocalDateTime.class));
  }

  @Test
  @DisplayName("Should handle empty product list")
  void shouldHandleEmptyProductList() {
    when(productRepository.findProductsNeedingSummaryUpdate(anyInt())).thenReturn(List.of());

    batchService.updateSummaries();

    verify(priceSummaryRepository, never()).saveAll(any());
    verify(productRepository, never()).updateSummaryLastCalculated(any(), any());
  }

  @Test
  @DisplayName("Should handle product with no prices")
  void shouldHandleProductWithNoPrices() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Test Product").build();
    when(productRepository.findProductsNeedingSummaryUpdate(anyInt()))
        .thenReturn(List.of(product))
        .thenReturn(List.of());
    when(priceRepository.findByProductId(1L)).thenReturn(List.of());

    batchService.updateSummaries();

    ArgumentCaptor<List<ProductPriceSummary>> summaryCaptor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(summaryCaptor.capture());

    ProductPriceSummary summary = summaryCaptor.getValue().get(0);
    assertEquals(1L, summary.getProductId());
    assertNull(summary.getAvgPrice());
    assertNotNull(summary.getLastCalculatedAt());
  }

  @Test
  @DisplayName("Should calculate statistics using all prices")
  void shouldCalculateStatisticsUsingAllPrices() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Test Product").build();
    when(productRepository.findProductsNeedingSummaryUpdate(anyInt()))
        .thenReturn(List.of(product))
        .thenReturn(List.of());

    LocalDate today = LocalDate.now();
    PriceDomain recentPrice =
        PriceDomain.builder()
            .id(1L)
            .productId(1L)
            .storeId(1L)
            .price(20.0)
            .dateRecorded(today.minusDays(30))
            .build();
    PriceDomain oldPrice =
        PriceDomain.builder()
            .id(2L)
            .productId(1L)
            .storeId(1L)
            .price(10.0)
            .dateRecorded(today.minusDays(100))
            .build();

    when(priceRepository.findByProductId(1L)).thenReturn(List.of(recentPrice, oldPrice));

    batchService.updateSummaries();

    ArgumentCaptor<List<ProductPriceSummary>> summaryCaptor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(summaryCaptor.capture());

    ProductPriceSummary summary = summaryCaptor.getValue().get(0);
    assertEquals(new BigDecimal("15.00"), summary.getAvgPrice());
    assertEquals(2, summary.getPriceCount());
  }

  @Test
  @DisplayName("Should handle null prices gracefully")
  void shouldHandleNullPrices() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Test Product").build();
    when(productRepository.findProductsNeedingSummaryUpdate(anyInt()))
        .thenReturn(List.of(product))
        .thenReturn(List.of());

    PriceDomain priceWithNull =
        PriceDomain.builder()
            .id(1L)
            .productId(1L)
            .storeId(1L)
            .price(null)
            .dateRecorded(LocalDate.now())
            .build();
    PriceDomain validPrice =
        PriceDomain.builder()
            .id(2L)
            .productId(1L)
            .storeId(1L)
            .price(10.0)
            .dateRecorded(LocalDate.now())
            .build();

    when(priceRepository.findByProductId(1L)).thenReturn(List.of(priceWithNull, validPrice));

    batchService.updateSummaries();

    ArgumentCaptor<List<ProductPriceSummary>> summaryCaptor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(summaryCaptor.capture());

    ProductPriceSummary summary = summaryCaptor.getValue().get(0);
    assertEquals(new BigDecimal("10.00"), summary.getAvgPrice());
    assertEquals(2, summary.getPriceCount());
  }

  @Test
  @DisplayName("Should count unique stores correctly")
  void shouldCountUniqueStoresCorrectly() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Test Product").build();
    when(productRepository.findProductsNeedingSummaryUpdate(anyInt()))
        .thenReturn(List.of(product))
        .thenReturn(List.of());

    LocalDate today = LocalDate.now();
    PriceDomain price1 =
        PriceDomain.builder()
            .id(1L)
            .productId(1L)
            .storeId(1L)
            .price(10.0)
            .dateRecorded(today)
            .build();
    PriceDomain price2 =
        PriceDomain.builder()
            .id(2L)
            .productId(1L)
            .storeId(1L)
            .price(12.0)
            .dateRecorded(today)
            .build();
    PriceDomain price3 =
        PriceDomain.builder()
            .id(3L)
            .productId(1L)
            .storeId(2L)
            .price(15.0)
            .dateRecorded(today)
            .build();

    when(priceRepository.findByProductId(1L)).thenReturn(List.of(price1, price2, price3));

    batchService.updateSummaries();

    ArgumentCaptor<List<ProductPriceSummary>> summaryCaptor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(summaryCaptor.capture());

    ProductPriceSummary summary = summaryCaptor.getValue().get(0);
    assertEquals(2, summary.getStoreCount());
    assertEquals(3, summary.getPriceCount());
  }

  @Test
  @DisplayName("Should handle multiple batches")
  void shouldHandleMultipleBatches() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Test Product").build();
    List<ProductDomain> firstBatch = java.util.Collections.nCopies(100, product);

    when(productRepository.findProductsNeedingSummaryUpdate(100))
        .thenReturn(firstBatch)
        .thenReturn(List.of());

    when(priceRepository.findByProductId(any())).thenReturn(List.of());

    batchService.updateSummaries();

    verify(productRepository, times(2)).findProductsNeedingSummaryUpdate(100);
    verify(priceSummaryRepository, times(1)).saveAll(any());
  }

  @Test
  @DisplayName("Should continue processing on individual product failure")
  void shouldContinueProcessingOnIndividualProductFailure() {
    ProductDomain product1 = ProductDomain.builder().id(1L).name("Product 1").build();
    ProductDomain product2 = ProductDomain.builder().id(2L).name("Product 2").build();
    when(productRepository.findProductsNeedingSummaryUpdate(anyInt()))
        .thenReturn(List.of(product1, product2))
        .thenReturn(List.of());

    when(priceRepository.findByProductId(1L)).thenThrow(new RuntimeException("Database error"));
    when(priceRepository.findByProductId(2L)).thenReturn(List.of());

    assertDoesNotThrow(() -> batchService.updateSummaries());

    verify(priceSummaryRepository, times(1)).saveAll(any());
    verify(productRepository).updateSummaryLastCalculated(eq(2L), any(LocalDateTime.class));
  }

  @Test
  @DisplayName("Should calculate last price date correctly")
  void shouldCalculateLastPriceDateCorrectly() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Test Product").build();
    when(productRepository.findProductsNeedingSummaryUpdate(anyInt()))
        .thenReturn(List.of(product))
        .thenReturn(List.of());

    LocalDate today = LocalDate.now();
    PriceDomain price1 =
        PriceDomain.builder()
            .id(1L)
            .productId(1L)
            .storeId(1L)
            .price(10.0)
            .dateRecorded(today.minusDays(10))
            .build();
    PriceDomain price2 =
        PriceDomain.builder()
            .id(2L)
            .productId(1L)
            .storeId(1L)
            .price(12.0)
            .dateRecorded(today.minusDays(5))
            .build();
    PriceDomain price3 =
        PriceDomain.builder()
            .id(3L)
            .productId(1L)
            .storeId(1L)
            .price(15.0)
            .dateRecorded(today.minusDays(20))
            .build();

    when(priceRepository.findByProductId(1L)).thenReturn(List.of(price1, price2, price3));

    batchService.updateSummaries();

    ArgumentCaptor<List<ProductPriceSummary>> summaryCaptor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(summaryCaptor.capture());

    ProductPriceSummary summary = summaryCaptor.getValue().get(0);
    assertEquals(today.minusDays(5), summary.getLastPriceDate());
  }
}
