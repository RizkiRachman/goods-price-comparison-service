package com.example.goodsprice.controller;

import com.example.goodsprice.api.model.ApiVersionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SystemController.
 */
class SystemControllerTest {

    private final SystemController controller = new SystemController();

    @Test
    @DisplayName("Should return API version with correct data")
    void shouldReturnApiVersion_WithCorrectData() {
        // Given
        // When
        ResponseEntity<ApiVersionResponse> response = controller.getApiVersion();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ApiVersionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("1.0.0", body.getVersion());
        assertEquals(ApiVersionResponse.StatusEnum.STABLE, body.getStatus());
    }
}
