package com.example.goodsprice.product.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.CreateProductRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Product;
import com.example.goodsprice.api.model.ProductListResponse;
import com.example.goodsprice.api.model.UpdateProductRequest;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.web.AbstractControllerWebMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class ProductControllerWebMvcTest extends AbstractControllerWebMvcTest {

  @Mock private ProductWebAdapter adapter;

  @Override
  protected Object getController() {
    return new ProductController(adapter);
  }

  private Product createApiProduct() {
    var product = new Product();
    product.setId(1L);
    product.setName("Susu Kotak");
    product.setCategory("Minuman");
    product.setBrand("Indomilk");
    product.setUnit("KG");
    product.setStatus(EntityStatus.APPROVED);
    return product;
  }

  @Test
  @DisplayName("POST /v1/products should return 201 Created")
  void shouldCreateProductReturns201() throws Exception {
    var product = createApiProduct();
    when(adapter.create(any(CreateProductRequest.class))).thenReturn(product);

    var request = new CreateProductRequest();
    request.setName("Susu Kotak");
    request.setCategory("Minuman");
    request.setBrand("Indomilk");
    request.setUnit("KG");

    mockMvc
        .perform(
            post("/v1/products").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Susu Kotak"));
  }

  @Test
  @DisplayName("GET /v1/products/{id} should return 200 OK")
  void shouldGetProductReturns200() throws Exception {
    var product = createApiProduct();
    when(adapter.findById(1L)).thenReturn(product);

    mockMvc
        .perform(get("/v1/products/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Susu Kotak"));
  }

  @Test
  @DisplayName("GET /v1/products should return 200 OK with paginated list")
  void shouldListProductsReturns200() throws Exception {
    var listResponse = new ProductListResponse();
    when(adapter.list(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(listResponse);

    mockMvc
        .perform(
            get("/v1/products")
                .param("page", "1")
                .param("pageSize", "20")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /v1/products/{id} should return 200 OK")
  void shouldUpdateProductReturns200() throws Exception {
    var product = createApiProduct();
    when(adapter.update(eq(1L), any(UpdateProductRequest.class))).thenReturn(product);

    var updateRequest = new UpdateProductRequest();
    updateRequest.setName("Susu Kotak Updated");

    mockMvc
        .perform(
            put("/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("DELETE /v1/products/{id} should return 204 No Content")
  void shouldDeleteProductReturns204() throws Exception {
    mockMvc
        .perform(delete("/v1/products/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("GET /v1/products/{id} should return 404 when product not found")
  void shouldReturn404WhenProductNotFound() throws Exception {
    when(adapter.findById(999L))
        .thenThrow(new NotFoundException("PRODUCT_NOT_FOUND", "Product not found with id: 999"));

    mockMvc
        .perform(get("/v1/products/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("PRODUCT_NOT_FOUND"));
  }

  @Test
  @DisplayName("POST /v1/products should return 400 when name is null")
  void shouldReturn400WhenNameIsNull() throws Exception {
    String invalidJson =
        """
        {"name": null, "category": "Minuman"}
        """;

    mockMvc
        .perform(post("/v1/products").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /v1/products/{id} should return correct JSON structure")
  void shouldReturnCorrectJsonStructure() throws Exception {
    var product = createApiProduct();
    when(adapter.findById(1L)).thenReturn(product);

    mockMvc
        .perform(get("/v1/products/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Susu Kotak"))
        .andExpect(jsonPath("$.category").value("Minuman"))
        .andExpect(jsonPath("$.brand").value("Indomilk"))
        .andExpect(jsonPath("$.status").value("approved"));
  }
}
