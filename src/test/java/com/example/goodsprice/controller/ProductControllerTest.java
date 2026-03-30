package com.example.goodsprice.controller;

import com.example.goodsprice.api.model.ProductTrendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductController.
 */
class ProductControllerTest {

    private final ProductController controller = new ProductController();

    @Test
    @DisplayName("Should get product trend with valid product ID")
    void shouldGetProductTrend_WithValidProductId() {
        // Given
        Long productId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        String granularity = "daily";

        // When
        ResponseEntity<ProductTrendResponse> response = controller.getProductTrend(
            productId, startDate, endDate, granularity
        );

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ProductTrendResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(productId, body.getProductId());
    }

    @Test
    @DisplayName("Should handle null product ID")
    void shouldHandleNullProductId() {
        // Given
        Long nullProductId = null;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        String granularity = "daily";

        // When & Then
        assertDoesNotThrow(() -> controller.getProductTrend(
            nullProductId, startDate, endDate, granularity
        ));
    }

    @Test
    @DisplayName("Should handle null dates")
    void shouldHandleNullDates() {
        // Given
        Long productId = 1L;
        LocalDate nullStartDate = null;
        LocalDate nullEndDate = null;
        String granularity = "daily";

        // When & Then
        assertDoesNotThrow(() -> controller.getProductTrend(
            productId, nullStartDate, nullEndDate, granularity
        ));
    }

    @Test
    @DisplayName("Should handle null granularity")
    void shouldHandleNullGranularity() {
        // Given
        Long productId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        String nullGranularity = null;

        // When & Then
        assertDoesNotThrow(() -> controller.getProductTrend(
            productId, startDate, endDate, nullGranularity
        ));
    }

    @Test
    @DisplayName("Should handle all null parameters")
    void shouldHandleAllNullParameters() {
        // When & Then
        assertDoesNotThrow(() -> controller.getProductTrend(
            null, null, null, null
        ));
    }
}
