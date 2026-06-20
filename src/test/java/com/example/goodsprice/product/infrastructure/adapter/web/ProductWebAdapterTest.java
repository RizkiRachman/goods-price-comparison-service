package com.example.goodsprice.product.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreateProductRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Product;
import com.example.goodsprice.api.model.ProductTrendResponse;
import com.example.goodsprice.api.model.UpdateProductRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.port.in.PriceSummaryInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.product.infrastructure.adapter.web.mapper.ProductDtoMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
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
class ProductWebAdapterTest {

  @Mock private ProductInPort productInPort;
  @Mock private PriceSummaryInPort priceSummaryInPort;
  @Mock private ProductDtoMapper mapper;

  @InjectMocks private ProductWebAdapter productWebAdapter;

  @Captor private ArgumentCaptor<ProductSearchCriteria> criteriaCaptor;

  private ProductDomain productDomain;
  private Product apiProduct;

  @BeforeEach
  void setUp() {
    productDomain =
        ProductDomain.builder()
            .id(1L)
            .name("Susu Kotak")
            .category("Minuman")
            .brand("Indomilk")
            .unit("KG")
            .status("ACTIVE")
            .build();

    apiProduct = new Product();
    apiProduct.setId(1L);
    apiProduct.setName("Susu Kotak");
  }

  @Test
  @DisplayName("Should create product from request")
  void shouldCreateProduct() {
    var request = new CreateProductRequest();
    request.setName("Susu Kotak");
    request.setCategory("Minuman");
    request.setBrand("Indomilk");
    request.setUnit("KG");

    when(productInPort.create(any(ProductDomain.class))).thenReturn(productDomain);
    when(mapper.toApiProduct(productDomain)).thenReturn(apiProduct);

    var result = productWebAdapter.create(request);

    assertNotNull(result);
    assertEquals("Susu Kotak", result.getName());
    verify(productInPort).create(any(ProductDomain.class));
  }

  @Test
  @DisplayName("Should find product by id")
  void shouldFindById() {
    when(productInPort.findById(1L)).thenReturn(productDomain);
    when(mapper.toApiProduct(productDomain)).thenReturn(apiProduct);

    var result = productWebAdapter.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Should list products with pagination")
  void shouldListProducts() {
    var pageResponse = PageResponse.of(List.of(productDomain), 0, 20, 1);
    when(productInPort.search(any())).thenReturn(pageResponse);
    when(mapper.toApiProduct(productDomain, null)).thenReturn(apiProduct);

    var result =
        productWebAdapter.list(1, 20, "name", "asc", "search", null, null, null, false, null);

    assertNotNull(result);
    assertEquals(
        1, ((com.example.goodsprice.api.model.ProductListResponse) result).getData().size());
  }

  @Test
  @DisplayName("Should update product")
  void shouldUpdateProduct() {
    var request = new UpdateProductRequest();
    request.setName("Updated Product");

    when(productInPort.update(eq(1L), any(ProductDomain.class))).thenReturn(productDomain);
    when(mapper.toApiProduct(productDomain)).thenReturn(apiProduct);

    var result = productWebAdapter.update(1L, request);

    assertNotNull(result);
    assertEquals("Susu Kotak", result.getName());
    verify(productInPort).update(eq(1L), any(ProductDomain.class));
  }

  @Test
  @DisplayName("Should delete product")
  void shouldDeleteProduct() {
    productWebAdapter.delete(1L);

    verify(productInPort).deleteById(1L);
  }

  @Test
  @DisplayName("Should list products with includePrice=true and summaries returned")
  void shouldListProductsWithIncludePriceAndSummaries() {
    var pageResponse = PageResponse.of(List.of(productDomain), 0, 20, 1);
    when(productInPort.search(any())).thenReturn(pageResponse);

    var summary =
        ProductPriceSummary.builder()
            .productId(1L)
            .avgPrice(new BigDecimal("15000"))
            .minPrice(new BigDecimal("14000"))
            .maxPrice(new BigDecimal("16000"))
            .storeCount(3)
            .priceCount(5)
            .build();
    when(priceSummaryInPort.findByProductIds(Set.of(1L))).thenReturn(List.of(summary));
    when(mapper.toApiProduct(productDomain, summary)).thenReturn(apiProduct);

    var result = productWebAdapter.list(1, 20, "name", "asc", null, null, null, null, true, null);

    assertNotNull(result);
    var listResponse = (com.example.goodsprice.api.model.ProductListResponse) result;
    assertEquals(1, listResponse.getData().size());
    verify(priceSummaryInPort).findByProductIds(Set.of(1L));
  }

  @Test
  @DisplayName("Should list products with includePrice=true and null summaries")
  void shouldListProductsWithIncludePriceAndNullSummaries() {
    var pageResponse = PageResponse.of(List.of(productDomain), 0, 20, 1);
    when(productInPort.search(any())).thenReturn(pageResponse);
    when(priceSummaryInPort.findByProductIds(Set.of(1L))).thenReturn(null);
    when(mapper.toApiProduct(productDomain, null)).thenReturn(apiProduct);

    var result = productWebAdapter.list(1, 20, "name", "asc", null, null, null, null, true, null);

    assertNotNull(result);
    var listResponse = (com.example.goodsprice.api.model.ProductListResponse) result;
    assertEquals(1, listResponse.getData().size());
    verify(priceSummaryInPort).findByProductIds(Set.of(1L));
  }

  @Test
  @DisplayName("Should list products with includePrice=true and empty content")
  void shouldListProductsWithIncludePriceAndEmptyContent() {
    var pageResponse = PageResponse.<ProductDomain>of(Collections.emptyList(), 0, 20, 0);
    when(productInPort.search(any())).thenReturn(pageResponse);

    var result = productWebAdapter.list(1, 20, "name", "asc", null, null, null, null, true, null);

    assertNotNull(result);
    var listResponse = (com.example.goodsprice.api.model.ProductListResponse) result;
    assertTrue(listResponse.getData().isEmpty());
  }

  @Test
  @DisplayName("Should list products with search filter")
  void shouldListProductsWithSearchFilter() {
    var pageResponse = PageResponse.<ProductDomain>of(Collections.emptyList(), 0, 20, 0);
    when(productInPort.search(criteriaCaptor.capture())).thenReturn(pageResponse);

    productWebAdapter.list(1, 20, "name", "asc", "susu", null, null, null, false, null);

    var criteria = criteriaCaptor.getValue();
    assertEquals("susu", criteria.getSearch());
  }

  @Test
  @DisplayName("Should list products with category filter")
  void shouldListProductsWithCategoryFilter() {
    var pageResponse = PageResponse.<ProductDomain>of(Collections.emptyList(), 0, 20, 0);
    when(productInPort.search(criteriaCaptor.capture())).thenReturn(pageResponse);

    productWebAdapter.list(1, 20, "name", "asc", null, null, "Minuman", null, false, null);

    var criteria = criteriaCaptor.getValue();
    assertEquals("Minuman", criteria.getCategory());
  }

  @Test
  @DisplayName("Should list products with brand filter")
  void shouldListProductsWithBrandFilter() {
    var pageResponse = PageResponse.<ProductDomain>of(Collections.emptyList(), 0, 20, 0);
    when(productInPort.search(criteriaCaptor.capture())).thenReturn(pageResponse);

    productWebAdapter.list(1, 20, "name", "asc", null, null, null, "Indomilk", false, null);

    var criteria = criteriaCaptor.getValue();
    assertEquals("Indomilk", criteria.getBrand());
  }

  @Test
  @DisplayName("Should list products with status filter")
  void shouldListProductsWithStatusFilter() {
    var pageResponse = PageResponse.<ProductDomain>of(Collections.emptyList(), 0, 20, 0);
    when(productInPort.search(criteriaCaptor.capture())).thenReturn(pageResponse);

    productWebAdapter.list(
        1, 20, "name", "asc", null, EntityStatus.APPROVED, null, null, false, null);

    var criteria = criteriaCaptor.getValue();
    assertEquals("approved", criteria.getStatus());
  }

  @Test
  @DisplayName("Should get product trend")
  void shouldGetTrend() {
    when(productInPort.findById(1L)).thenReturn(productDomain);

    var result =
        productWebAdapter.getTrend(1L, LocalDate.now(), LocalDate.now().plusDays(7), "daily");

    assertNotNull(result);
    assertTrue(result instanceof ProductTrendResponse);
    assertEquals(productDomain.getId(), result.getProductId());
    assertEquals(productDomain.getName(), result.getProductName());
    verify(productInPort).findById(1L);
  }
}
