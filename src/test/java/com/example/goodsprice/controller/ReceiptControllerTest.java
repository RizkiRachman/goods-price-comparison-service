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
 * Unit tests for ReceiptController with async processing.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ReceiptControllerTest {

    @Autowired
    private ReceiptController controller;

    @Test
    @DisplayName("Should upload receipt and return PROCESSING status")
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
        // Now returns PROCESSING (PENDING) instead of COMPLETED
        assertEquals(ReceiptUploadResponse.StatusEnum.PROCESSING, body.getStatus());
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

        // When & Then - should not throw exception, just return PROCESSING
        assertDoesNotThrow(() -> {
            ResponseEntity<ReceiptStatusResponse> response = controller.getReceiptStatus(nullId);
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
        });
    }

    @Test
    @DisplayName("Should handle null receipt ID in results check")
    void shouldHandleNullReceiptId_InResultsCheck() {
        // Given
        UUID nullId = null;

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> {
            ResponseEntity<ReceiptResultResponse> response = controller.getReceiptResults(nullId);
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
        });
    }

    @Test
    @DisplayName("Should handle duplicate receipt upload")
    void shouldHandleDuplicateReceiptUpload() {
        // Given - upload same image twice
        byte[] imageContent = "duplicate-test-content".getBytes();
        MultipartFile image1 = new MockMultipartFile(
            "image", "receipt1.jpg", "image/jpeg", imageContent
        );
        MultipartFile image2 = new MockMultipartFile(
            "image", "receipt2.jpg", "image/jpeg", imageContent
        );

        // When - first upload
        ResponseEntity<ReceiptUploadResponse> response1 = controller.uploadReceipt(image1);
        
        // When - second upload (same content)
        ResponseEntity<ReceiptUploadResponse> response2 = controller.uploadReceipt(image2);

        // Then
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(response1.getBody().getJobId(), response2.getBody().getJobId());
        assertEquals(HttpStatus.OK, response2.getStatusCode()); // Duplicate returns OK, not ACCEPTED
    }
}
