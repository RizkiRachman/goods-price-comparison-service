package com.example.goodsprice.product.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreateProductRequest;
import com.example.goodsprice.api.model.Product;
import com.example.goodsprice.api.model.ProductTrendResponse;
import com.example.goodsprice.api.model.UpdateProductRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  @Mock private ProductWebAdapter adapter;

  @InjectMocks private ProductController controller;

  private Product apiProduct;

  @BeforeEach
  void setUp() {
    apiProduct = new Product();
    apiProduct.setId(1L);
    apiProduct.setName("Susu Kotak");
  }

  @Test
  @DisplayName("Should create product via controller")
  void shouldCreateProduct() {
    var request = new CreateProductRequest();
    request.setName("Susu Kotak");

    when(adapter.create(request)).thenReturn(apiProduct);

    var response = controller.createProduct(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
    verify(adapter).create(request);
  }

  @Test
  @DisplayName("Should get product by id")
  void shouldGetProduct() {
    when(adapter.findById(1L)).thenReturn(apiProduct);

    var response = controller.getProduct(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
    verify(adapter).findById(1L);
  }

  @Test
  @DisplayName("Should list products")
  void shouldListProducts() {
    var response =
        controller.listProducts(
            1, 20, "search", "cat", "brand", null, "name", "asc", false, 1L, null, null, null,
            null);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("Should update product")
  void shouldUpdateProduct() {
    var request = new UpdateProductRequest();
    request.setName("Updated");

    when(adapter.update(1L, request)).thenReturn(apiProduct);

    var response = controller.updateProduct(1L, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapter).update(1L, request);
  }

  @Test
  @DisplayName("Should delete product")
  void shouldDeleteProduct() {
    var response = controller.deleteProduct(1L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(adapter).delete(1L);
  }

  @Test
  @DisplayName("Should get product trend")
  void shouldGetProductTrend() {
    var trendResponse = new ProductTrendResponse();
    trendResponse.setProductId(1L);
    trendResponse.setProductName("Susu Kotak");

    when(adapter.getTrend(1L, LocalDate.now(), LocalDate.now().plusDays(7), "daily"))
        .thenReturn(trendResponse);

    var response =
        controller.getProductTrend(1L, LocalDate.now(), LocalDate.now().plusDays(7), "daily");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().getProductId());
    verify(adapter).getTrend(1L, LocalDate.now(), LocalDate.now().plusDays(7), "daily");
  }
}
