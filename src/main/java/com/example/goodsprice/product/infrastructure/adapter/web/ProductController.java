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
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

  private final ProductWebAdapter adapter;

  @Override
  public ResponseEntity<Product> createProduct(@Valid CreateProductRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
  }

  @Override
  public ResponseEntity<Product> getProduct(Long id) {
    return ResponseEntity.ok(adapter.findById(id));
  }

  @Override
  public ResponseEntity<ProductTrendResponse> getProductTrend(
      Long productId, LocalDate startDate, LocalDate endDate, String granularity) {
    return ResponseEntity.ok(adapter.getTrend(productId, startDate, endDate, granularity));
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
    return ResponseEntity.ok(
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
            Objects.nonNull(storeId) ? String.valueOf(storeId) : null));
  }

  @Override
  public ResponseEntity<Product> updateProduct(Long id, @Valid UpdateProductRequest request) {
    return ResponseEntity.ok(adapter.update(id, request));
  }

  @Override
  public ResponseEntity<Void> deleteProduct(Long id) {
    adapter.delete(id);
    return ResponseEntity.noContent().build();
  }
}
