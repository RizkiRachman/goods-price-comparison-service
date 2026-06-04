package com.example.goodsprice.product.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.ProductsApi;
import com.example.goodsprice.api.model.CreateProductRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.ListProducts200Response;
import com.example.goodsprice.api.model.Product;
import com.example.goodsprice.api.model.ProductTrendResponse;
import com.example.goodsprice.api.model.UpdateProductRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

  private final ProductWebAdapter adapter;

  @Override
  public ResponseEntity<Product> createProduct(@Valid CreateProductRequest request) {
    var product = adapter.create(request);
    return ResponseEntity.ok(product);
  }

  @Override
  public ResponseEntity<Product> getProduct(Long id) {
    var product = adapter.findById(id);
    return ResponseEntity.ok(product);
  }

  @Override
  public ResponseEntity<ProductTrendResponse> getProductTrend(
      Long productId, LocalDate startDate, LocalDate endDate, String granularity) {
    var response = adapter.getTrend(productId, startDate, endDate, granularity);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<ListProducts200Response> listProducts(
      Integer page,
      Integer pageSize,
      String search,
      String category,
      String brand,
      EntityStatus status,
      String sortBy,
      String sortOrder,
      Boolean includePrice,
      Long storeId,
      Double minPrice,
      Double maxPrice,
      Boolean isPromo,
      String availability) {
    var response =
        adapter.list(
            page,
            pageSize,
            sortBy,
            sortOrder,
            search,
            status,
            category,
            brand,
            includePrice,
            storeId != null ? String.valueOf(storeId) : null);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Product> updateProduct(Long id, @Valid UpdateProductRequest request) {
    var product = adapter.update(id, request);
    return ResponseEntity.ok(product);
  }

  @Override
  public ResponseEntity<Void> deleteProduct(Long id) {
    adapter.delete(id);
    return ResponseEntity.noContent().build();
  }
}
