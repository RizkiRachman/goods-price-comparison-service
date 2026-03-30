package com.example.goodsprice.controller;

import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.api.model.PriceSearchResponse;
import com.example.goodsprice.api.model.PriceSearchResponseV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PriceController.
 */
class PriceControllerTest {

    private final PriceController controller = new PriceController();

    @Test
    @DisplayName("Should search prices with v1 request")
    void shouldSearchPrices_WithV1Request() {
        // Given
        PriceSearchRequest request = new PriceSearchRequest();
        request.setProductName("Milk");
        request.setLocation("Jakarta");

        // When
        ResponseEntity<PriceSearchResponse> response = controller.searchPrices(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should search prices with v2 request")
    void shouldSearchPrices_WithV2Request() {
        // Given
        PriceSearchRequestV2 request = new PriceSearchRequestV2();
        request.setProductName("Milk");

        // When
        ResponseEntity<PriceSearchResponseV2> response = controller.searchPricesV2(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle null v1 request")
    void shouldHandleNullV1Request() {
        // Given
        PriceSearchRequest nullRequest = null;

        // When & Then
        assertDoesNotThrow(() -> controller.searchPrices(nullRequest));
    }

    @Test
    @DisplayName("Should handle null v2 request")
    void shouldHandleNullV2Request() {
        // Given
        PriceSearchRequestV2 nullRequest = null;

        // When & Then
        assertDoesNotThrow(() -> controller.searchPricesV2(nullRequest));
    }

    @Test
    @DisplayName("Should handle empty v1 request")
    void shouldHandleEmptyV1Request() {
        // Given
        PriceSearchRequest emptyRequest = new PriceSearchRequest();

        // When
        ResponseEntity<PriceSearchResponse> response = controller.searchPrices(emptyRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle empty v2 request")
    void shouldHandleEmptyV2Request() {
        // Given
        PriceSearchRequestV2 emptyRequest = new PriceSearchRequestV2();

        // When
        ResponseEntity<PriceSearchResponseV2> response = controller.searchPricesV2(emptyRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}
