package com.example.goodsprice.controller;

import com.example.goodsprice.api.model.ShoppingOptimizeRequest;
import com.example.goodsprice.api.model.ShoppingOptimizeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ShoppingController.
 */
class ShoppingControllerTest {

    private final ShoppingController controller = new ShoppingController();

    @Test
    @DisplayName("Should optimize shopping route with valid request")
    void shouldOptimizeShoppingRoute_WithValidRequest() {
        // Given
        ShoppingOptimizeRequest request = new ShoppingOptimizeRequest();
        // TODO: Add items to request when model is fully implemented

        // When
        ResponseEntity<ShoppingOptimizeResponse> response = controller.optimizeShoppingRoute(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle null request")
    void shouldHandleNullRequest() {
        // Given
        ShoppingOptimizeRequest nullRequest = null;

        // When & Then
        assertDoesNotThrow(() -> controller.optimizeShoppingRoute(nullRequest));
    }

    @Test
    @DisplayName("Should handle empty request")
    void shouldHandleEmptyRequest() {
        // Given
        ShoppingOptimizeRequest emptyRequest = new ShoppingOptimizeRequest();

        // When
        ResponseEntity<ShoppingOptimizeResponse> response = controller.optimizeShoppingRoute(emptyRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}
