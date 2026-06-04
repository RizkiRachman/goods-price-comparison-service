package com.example.goodsprice.price.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

@ExtendWith(MockitoExtension.class)
class PriceBatchProcessorTest {

  @Mock private PriceRepositoryPort priceRepository;
  @Mock private PriceSummaryRepositoryPort priceSummaryRepository;
  @Mock private ProductRepositoryPort productRepository;

  private PriceBatchProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new PriceBatchProcessor(priceRepository, priceSummaryRepository, productRepository);
  }

  @Test
  @DisplayName("Should calculate statistics using unit price for weight units")
  void shouldUseUnitPriceForWeightUnits() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Gula").unit("KG").build();
    when(priceRepository.findAllByProductIds(List.of(1L)))
        .thenReturn(
            List.of(
                PriceDomain.builder()
                    .id(1L)
                    .productId(1L)
                    .storeId(1L)
                    .price(20000.0)
                    .unitPrice(10000.0)
                    .dateRecorded(LocalDate.now())
                    .isPromo(false)
                    .build(),
                PriceDomain.builder()
                    .id(2L)
                    .productId(1L)
                    .storeId(2L)
                    .price(24000.0)
                    .unitPrice(12000.0)
                    .dateRecorded(LocalDate.now())
                    .isPromo(false)
                    .build()));

    processor.processProductBatch(List.of(product));

    ArgumentCaptor<List<ProductPriceSummary>> captor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(captor.capture());

    ProductPriceSummary summary = captor.getValue().getFirst();
    assertEquals(new BigDecimal("11000.00"), summary.getAvgPrice());
    assertEquals(new BigDecimal("10000.00"), summary.getMinPrice());
    assertEquals(new BigDecimal("12000.00"), summary.getMaxPrice());
  }

  @Test
  @DisplayName("Should use regular price for non-weight units")
  void shouldUseRegularPriceForNonWeightUnits() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Susu").unit("LITER").build();
    when(priceRepository.findAllByProductIds(List.of(1L)))
        .thenReturn(
            List.of(
                PriceDomain.builder()
                    .id(1L)
                    .productId(1L)
                    .storeId(1L)
                    .price(15000.0)
                    .unitPrice(15000.0)
                    .dateRecorded(LocalDate.now())
                    .isPromo(false)
                    .build(),
                PriceDomain.builder()
                    .id(2L)
                    .productId(1L)
                    .storeId(2L)
                    .price(18000.0)
                    .unitPrice(18000.0)
                    .dateRecorded(LocalDate.now())
                    .isPromo(false)
                    .build()));

    processor.processProductBatch(List.of(product));

    ArgumentCaptor<List<ProductPriceSummary>> captor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(captor.capture());

    ProductPriceSummary summary = captor.getValue().getFirst();
    assertEquals(new BigDecimal("16500.00"), summary.getAvgPrice());
    assertEquals(new BigDecimal("15000.00"), summary.getMinPrice());
    assertEquals(new BigDecimal("18000.00"), summary.getMaxPrice());
    assertEquals(2, summary.getPriceCount());
  }

  @Test
  @DisplayName("Should filter null product IDs")
  void shouldFilterNullProductIds() {
    ProductDomain valid = ProductDomain.builder().id(1L).name("Valid").build();
    ProductDomain nullId = ProductDomain.builder().id(null).name("Null ID").build();
    when(priceRepository.findAllByProductIds(List.of(1L))).thenReturn(List.of());

    processor.processProductBatch(List.of(valid, nullId));

    verify(priceRepository).findAllByProductIds(List.of(1L));
  }

  @Test
  @DisplayName("Should return empty statistics for all-zero prices")
  void shouldReturnEmptyStatisticsForAllZeroPrices() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Free Item").build();
    when(priceRepository.findAllByProductIds(List.of(1L)))
        .thenReturn(
            List.of(
                PriceDomain.builder()
                    .id(1L)
                    .productId(1L)
                    .storeId(1L)
                    .price(0.0)
                    .unitPrice(0.0)
                    .dateRecorded(LocalDate.now())
                    .isPromo(false)
                    .build()));

    processor.processProductBatch(List.of(product));

    ArgumentCaptor<List<ProductPriceSummary>> captor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(captor.capture());

    ProductPriceSummary summary = captor.getValue().getFirst();
    assertNull(summary.getAvgPrice());
    assertNull(summary.getMinPrice());
    assertNull(summary.getMaxPrice());
    assertEquals(0, summary.getStoreCount());
    assertEquals(0, summary.getPriceCount());
  }

  @Test
  @DisplayName("Should handle null unit gracefully")
  void shouldHandleNullUnit() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Product").unit(null).build();
    when(priceRepository.findAllByProductIds(List.of(1L)))
        .thenReturn(
            List.of(
                PriceDomain.builder()
                    .id(1L)
                    .productId(1L)
                    .storeId(1L)
                    .price(10000.0)
                    .unitPrice(5000.0)
                    .dateRecorded(LocalDate.now())
                    .isPromo(false)
                    .build()));

    processor.processProductBatch(List.of(product));

    ArgumentCaptor<List<ProductPriceSummary>> captor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(captor.capture());

    ProductPriceSummary summary = captor.getValue().getFirst();
    assertEquals(new BigDecimal("10000.00"), summary.getAvgPrice());
  }

  @Test
  @DisplayName("Should handle empty price list per product")
  void shouldHandleEmptyPriceList() {
    ProductDomain product = ProductDomain.builder().id(1L).name("No Prices").build();
    when(priceRepository.findAllByProductIds(List.of(1L))).thenReturn(List.of());

    processor.processProductBatch(List.of(product));

    ArgumentCaptor<List<ProductPriceSummary>> captor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(captor.capture());

    ProductPriceSummary summary = captor.getValue().getFirst();
    assertEquals(1L, summary.getProductId());
    assertNull(summary.getAvgPrice());
    assertNull(summary.getMinPrice());
    assertNull(summary.getMaxPrice());
  }

  @Test
  @DisplayName("Should skip products that throw exceptions")
  void shouldSkipProductsThatThrowExceptions() {
    ProductDomain bad = ProductDomain.builder().id(null).name("Bad").build();
    ProductDomain good = ProductDomain.builder().id(2L).name("Good").build();
    when(priceRepository.findAllByProductIds(anyList())).thenReturn(List.of());

    processor.processProductBatch(List.of(bad, good));

    verify(priceSummaryRepository).saveAll(any());
  }

  @Test
  @DisplayName("Should update summary last calculated time after processing")
  void shouldUpdateSummaryLastCalculated() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Test").build();
    when(priceRepository.findAllByProductIds(List.of(1L))).thenReturn(List.of());

    processor.processProductBatch(List.of(product));

    verify(productRepository).updateSummaryLastCalculated(anyList(), any(LocalDateTime.class));
  }

  @Test
  @DisplayName("Should handle null price values in calculateStatistics")
  void shouldHandleNullPriceValues() {
    ProductDomain product = ProductDomain.builder().id(1L).name("Null Prices").build();
    when(priceRepository.findAllByProductIds(List.of(1L)))
        .thenReturn(
            List.of(
                PriceDomain.builder()
                    .id(1L)
                    .productId(1L)
                    .storeId(1L)
                    .price(null)
                    .unitPrice(null)
                    .dateRecorded(LocalDate.now())
                    .isPromo(false)
                    .build(),
                PriceDomain.builder()
                    .id(2L)
                    .productId(1L)
                    .storeId(1L)
                    .price(10000.0)
                    .unitPrice(10000.0)
                    .dateRecorded(LocalDate.now())
                    .isPromo(false)
                    .build()));

    processor.processProductBatch(List.of(product));

    ArgumentCaptor<List<ProductPriceSummary>> captor = ArgumentCaptor.forClass(List.class);
    verify(priceSummaryRepository).saveAll(captor.capture());

    ProductPriceSummary summary = captor.getValue().getFirst();
    assertEquals(new BigDecimal("10000.00"), summary.getAvgPrice());
    assertEquals(new BigDecimal("10000.00"), summary.getMinPrice());
    assertEquals(new BigDecimal("10000.00"), summary.getMaxPrice());
  }
}
