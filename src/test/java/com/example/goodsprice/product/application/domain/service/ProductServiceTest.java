package com.example.goodsprice.product.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.port.in.PriceSummaryInPort;
import com.example.goodsprice.product.application.port.in.ProductPriceQueryInPort;
import com.example.goodsprice.product.application.port.in.StoreLookupInPort;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock private ProductRepositoryPort productRepository;
  @Mock private PriceSummaryInPort priceSummaryInPort;
  @Mock private ProductPriceQueryInPort productPriceQueryInPort;
  @Mock private StoreLookupInPort storeLookupInPort;

  @InjectMocks private ProductService productService;

  @Captor private ArgumentCaptor<ProductSearchCriteria> criteriaCaptor;

  private ProductDomain product1;
  private ProductDomain product2;

  @BeforeEach
  void setUp() {
    product1 = ProductDomain.builder().id(1L).name("Apple").category("Fruit").build();
    product2 = ProductDomain.builder().id(2L).name("Banana").category("Fruit").build();
  }

  @Test
  @DisplayName("Should search without storeId and call repository directly")
  void searchWithoutStoreIdShouldCallRepositoryDirectly() {
    var criteria = ProductSearchCriteria.builder().search("apple").build();
    var expectedPage =
        PageResponse.of(List.of(product1), criteria.getPage(), criteria.getSize(), 1);
    when(productRepository.search(criteria)).thenReturn(expectedPage);

    var result = productService.search(criteria);

    assertEquals(1, result.totalElements());
    assertEquals("Apple", result.content().get(0).getName());
    verify(productRepository).search(criteria);
    verify(productPriceQueryInPort, never()).findProductIdsByStoreIds(any());
  }

  @Test
  @DisplayName("Should pre-fetch product IDs for numeric storeId with matching products")
  void searchWithNumericStoreIdAndMatchingProductsShouldPreFetchProductIds() {
    var criteria = ProductSearchCriteria.builder().storeId("5").search("apple").build();
    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(5L))).thenReturn(List.of(1L, 2L));

    var expectedPage =
        PageResponse.of(List.of(product1, product2), criteria.getPage(), criteria.getSize(), 2);
    when(productRepository.search(any())).thenReturn(expectedPage);

    var result = productService.search(criteria);

    assertEquals(2, result.totalElements());
    verify(productRepository).search(criteriaCaptor.capture());
    var capturedCriteria = criteriaCaptor.getValue();
    assertTrue(capturedCriteria.hasProductIds());
    assertEquals(List.of(1L, 2L), capturedCriteria.getProductIds());
    assertEquals("apple", capturedCriteria.getSearch());
  }

  @Test
  @DisplayName("Should return empty page for numeric storeId with no matching products")
  void searchWithNumericStoreIdAndNoMatchingProductsShouldReturnEmptyPage() {
    var criteria = ProductSearchCriteria.builder().storeId("999").build();
    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(999L))).thenReturn(List.of());

    var result = productService.search(criteria);

    assertTrue(result.content().isEmpty());
    assertEquals(0, result.totalElements());
    verify(productRepository, never()).search(any());
  }

  @Test
  @DisplayName("Should resolve store name and pre-fetch product IDs when stores found")
  void searchWithStoreNameAndStoresFoundShouldResolveAndPreFetchProductIds() {
    var criteria = ProductSearchCriteria.builder().storeId("Toko Segar").build();
    when(storeLookupInPort.findStoreIdsByName("Toko Segar")).thenReturn(List.of(1L, 2L));
    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(1L, 2L))).thenReturn(List.of(1L));

    var expectedPage =
        PageResponse.of(List.of(product1), criteria.getPage(), criteria.getSize(), 1);
    when(productRepository.search(any())).thenReturn(expectedPage);

    var result = productService.search(criteria);

    assertEquals(1, result.totalElements());
    verify(productRepository).search(criteriaCaptor.capture());
    assertTrue(criteriaCaptor.getValue().hasProductIds());
    assertEquals(List.of(1L), criteriaCaptor.getValue().getProductIds());
  }

  @Test
  @DisplayName("Should return empty page when store name not found")
  void searchWithStoreNameAndNoStoresFoundShouldReturnEmptyPage() {
    var criteria = ProductSearchCriteria.builder().storeId("Unknown Store").build();
    when(storeLookupInPort.findStoreIdsByName("Unknown Store")).thenReturn(List.of());

    var result = productService.search(criteria);

    assertTrue(result.content().isEmpty());
    assertEquals(0, result.totalElements());
    verify(productPriceQueryInPort, never()).findProductIdsByStoreIds(any());
    verify(productRepository, never()).search(any());
  }

  @Test
  @DisplayName("Should return empty page when price query returns null")
  void searchWithStoreIdAndPriceQueryReturnsNullShouldReturnEmptyPage() {
    var criteria = ProductSearchCriteria.builder().storeId("5").build();
    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(5L))).thenReturn(null);

    var result = productService.search(criteria);

    assertTrue(result.content().isEmpty());
    assertEquals(0, result.totalElements());
    verify(productRepository, never()).search(any());
  }

  @Test
  @DisplayName("Should populate price summaries when includePrice is true with storeId")
  void searchWithStoreIdAndIncludePriceShouldPopulatePriceSummaries() {
    var criteria = ProductSearchCriteria.builder().storeId("5").build();
    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(5L))).thenReturn(List.of(1L));
    var productWithPrice = ProductDomain.builder().id(1L).name("Apple").category("Fruit").build();
    var expectedPage =
        PageResponse.of(List.of(productWithPrice), criteria.getPage(), criteria.getSize(), 1);
    when(productRepository.search(any())).thenReturn(expectedPage);

    var summary =
        ProductPriceSummary.builder()
            .productId(1L)
            .avgPrice(new BigDecimal("10.00"))
            .minPrice(new BigDecimal("8.00"))
            .maxPrice(new BigDecimal("12.00"))
            .lastCalculatedAt(LocalDateTime.of(2026, 5, 29, 10, 0))
            .build();
    when(priceSummaryInPort.findByProductIds(Set.of(1L))).thenReturn(List.of(summary));

    var result = productService.search(criteria, true);

    assertEquals(1, result.totalElements());
    var resultProduct = result.content().get(0);
    assertEquals(0, new BigDecimal("10.00").compareTo(resultProduct.getAvgPrice()));
    assertEquals(0, new BigDecimal("8.00").compareTo(resultProduct.getMinPrice()));
    assertEquals(0, new BigDecimal("12.00").compareTo(resultProduct.getMaxPrice()));
    assertEquals(LocalDateTime.of(2026, 5, 29, 10, 0), resultProduct.getPriceUpdatedAt());
  }

  @Test
  @DisplayName("Should populate price summaries without storeId")
  void searchWithoutStoreIdAndIncludePriceShouldPopulatePriceSummaries() {
    var criteria = ProductSearchCriteria.builder().search("apple").build();
    var productWithPrice = ProductDomain.builder().id(1L).name("Apple").category("Fruit").build();
    var expectedPage =
        PageResponse.of(List.of(productWithPrice), criteria.getPage(), criteria.getSize(), 1);
    when(productRepository.search(criteria)).thenReturn(expectedPage);

    var summary =
        ProductPriceSummary.builder().productId(1L).avgPrice(new BigDecimal("15.00")).build();
    when(priceSummaryInPort.findByProductIds(Set.of(1L))).thenReturn(List.of(summary));

    var result = productService.search(criteria, true);

    assertEquals(1, result.totalElements());
    assertEquals(0, new BigDecimal("15.00").compareTo(result.content().get(0).getAvgPrice()));
  }

  @Test
  @DisplayName("Should handle storeId 0 as valid numeric edge case")
  void searchWithStoreIdZeroShouldHandleNumericEdgeCase() {
    var criteria = ProductSearchCriteria.builder().storeId("0").build();
    when(productPriceQueryInPort.findProductIdsByStoreIds(List.of(0L))).thenReturn(List.of(1L));
    var expectedPage =
        PageResponse.of(List.of(product1), criteria.getPage(), criteria.getSize(), 1);
    when(productRepository.search(any())).thenReturn(expectedPage);

    var result = productService.search(criteria);

    assertEquals(1, result.totalElements());
    verify(productRepository).search(criteriaCaptor.capture());
    assertEquals(List.of(1L), criteriaCaptor.getValue().getProductIds());
  }

  @Test
  @DisplayName("Should not trigger store lookup for blank storeId")
  void searchWithBlankStoreIdShouldNotTriggerStoreLookup() {
    var criteria = ProductSearchCriteria.builder().storeId("  ").search("apple").build();
    var expectedPage =
        PageResponse.of(List.of(product1), criteria.getPage(), criteria.getSize(), 1);
    when(productRepository.search(criteria)).thenReturn(expectedPage);

    var result = productService.search(criteria);

    assertEquals(1, result.totalElements());
    verify(productRepository).search(criteria);
    verify(productPriceQueryInPort, never()).findProductIdsByStoreIds(any());
    verify(storeLookupInPort, never()).findStoreIdsByName(any());
  }
}
