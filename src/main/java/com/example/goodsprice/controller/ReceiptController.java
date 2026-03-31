package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.ReceiptsApi;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import com.example.goodsprice.service.receipt.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller for receipt upload and OCR processing.
 * Integrates with Gemini LLM for receipt extraction.
 */
@RestController
@RequiredArgsConstructor
public class ReceiptController implements ReceiptsApi {

    private final ReceiptService receiptService;

    // Simple in-memory storage for receipt results
    private final Map<UUID, Map<String, Object>> receiptStorage = new ConcurrentHashMap<>();

    @Override
    public ResponseEntity<ReceiptUploadResponse> uploadReceipt(MultipartFile image) {
        // Process receipt immediately using Gemini
        Map<String, Object> result = receiptService.processReceipt(image);

        // Store result
        UUID receiptId = UUID.fromString((String) result.get("receiptId"));
        receiptStorage.put(receiptId, result);

        // Build response
        ReceiptUploadResponse response = new ReceiptUploadResponse();
        response.setJobId(receiptId);
        response.setStatus(ReceiptUploadResponse.StatusEnum.COMPLETED);

        return ResponseEntity.accepted().body(response);
    }

    @Override
    public ResponseEntity<ReceiptStatusResponse> getReceiptStatus(UUID id) {
        ReceiptStatusResponse response = new ReceiptStatusResponse();
        response.setJobId(id);

        if (id != null && receiptStorage.containsKey(id)) {
            response.setStatus(ReceiptStatusResponse.StatusEnum.COMPLETED);
        } else {
            response.setStatus(ReceiptStatusResponse.StatusEnum.PROCESSING);
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReceiptResultResponse> getReceiptResults(UUID id) {
        ReceiptResultResponse response = new ReceiptResultResponse();
        response.setJobId(id);

        if (id != null) {
            Map<String, Object> result = receiptStorage.get(id);
            if (result != null) {
                // Map the result data to response
                response.setStoreName((String) result.get("storeName"));
                response.setStoreLocation((String) result.get("storeLocation"));
                // Date conversion would be needed here - skipping for now
                // response.setDate((String) result.get("date"));
                // Note: Items list mapping would need proper DTO conversion
            }
        }

        return ResponseEntity.ok(response);
    }
}
