package com.example.goodsprice.controller;

import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReceiptController.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ReceiptControllerTest {

    @Autowired
    private ReceiptController controller;

    @Test
    @DisplayName("Should upload receipt and return processing status")
    void shouldUploadReceipt_AndReturnProcessingStatus() {
        // Given
        MultipartFile image = new MockMultipartFile(
            "image", "receipt.jpg", "image/jpeg", "test-image-content".getBytes()
        );

        // When
        ResponseEntity<ReceiptUploadResponse> response = controller.uploadReceipt(image);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        ReceiptUploadResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getJobId());
        assertEquals(ReceiptUploadResponse.StatusEnum.COMPLETED, body.getStatus());
    }

    @Test
    @DisplayName("Should get receipt status for valid ID")
    void shouldGetReceiptStatus_ForValidId() {
        // Given
        UUID receiptId = UUID.randomUUID();

        // When
        ResponseEntity<ReceiptStatusResponse> response = controller.getReceiptStatus(receiptId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ReceiptStatusResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(receiptId, body.getJobId());
    }

    @Test
    @DisplayName("Should get receipt results for valid ID")
    void shouldGetReceiptResults_ForValidId() {
        // Given
        UUID receiptId = UUID.randomUUID();

        // When
        ResponseEntity<ReceiptResultResponse> response = controller.getReceiptResults(receiptId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ReceiptResultResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(receiptId, body.getJobId());
    }

    @Test
    @DisplayName("Should handle null receipt ID in status check")
    void shouldHandleNullReceiptId_InStatusCheck() {
        // Given
        UUID nullId = null;

        // When & Then
        assertDoesNotThrow(() -> controller.getReceiptStatus(nullId));
    }

    @Test
    @DisplayName("Should handle null receipt ID in results check")
    void shouldHandleNullReceiptId_InResultsCheck() {
        // Given
        UUID nullId = null;

        // When & Then
        assertDoesNotThrow(() -> controller.getReceiptResults(nullId));
    }
}
