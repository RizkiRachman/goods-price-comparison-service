package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.ProductsApi;
import com.example.goodsprice.api.model.ProductTrendResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controller for product information and trends.
 */
@RestController
public class ProductController implements ProductsApi {

    @Override
    public ResponseEntity<ProductTrendResponse> getProductTrend(Long productId, LocalDate startDate, LocalDate endDate, String granularity) {
        // TODO: Implement product trend retrieval logic
        ProductTrendResponse response = new ProductTrendResponse();
        response.setProductId(productId);
        return ResponseEntity.ok(response);
    }
}
