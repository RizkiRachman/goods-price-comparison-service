package com.example.goodsprice.controller;

import com.example.goodsprice.api.model.AlertSubscriptionRequest;
import com.example.goodsprice.api.model.AlertSubscriptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AlertController.
 */
class AlertControllerTest {

    private final AlertController controller = new AlertController();

    @Test
    @DisplayName("Should subscribe to alert with valid request")
    void shouldSubscribeToAlert_WithValidRequest() {
        // Given
        AlertSubscriptionRequest request = new AlertSubscriptionRequest();
        // TODO: Set request properties when model is fully implemented

        // When
        ResponseEntity<AlertSubscriptionResponse> response = controller.subscribeToAlert(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        AlertSubscriptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(AlertSubscriptionResponse.StatusEnum.ACTIVE, body.getStatus());
    }

    @Test
    @DisplayName("Should handle null request")
    void shouldHandleNullRequest() {
        // Given
        AlertSubscriptionRequest nullRequest = null;

        // When & Then
        assertDoesNotThrow(() -> controller.subscribeToAlert(nullRequest));
    }

    @Test
    @DisplayName("Should handle empty request")
    void shouldHandleEmptyRequest() {
        // Given
        AlertSubscriptionRequest emptyRequest = new AlertSubscriptionRequest();

        // When
        ResponseEntity<AlertSubscriptionResponse> response = controller.subscribeToAlert(emptyRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        AlertSubscriptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(AlertSubscriptionResponse.StatusEnum.ACTIVE, body.getStatus());
    }
}
