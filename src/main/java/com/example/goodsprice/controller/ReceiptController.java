package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.ReceiptsApi;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Controller for receipt upload and OCR processing.
 */
@RestController
public class ReceiptController implements ReceiptsApi {

    @Override
    public ResponseEntity<ReceiptUploadResponse> uploadReceipt(MultipartFile image) {
        // TODO: Implement receipt upload logic
        ReceiptUploadResponse response = new ReceiptUploadResponse();
        response.setJobId(UUID.randomUUID());
        response.setStatus(ReceiptUploadResponse.StatusEnum.PROCESSING);
        return ResponseEntity.accepted().body(response);
    }

    @Override
    public ResponseEntity<ReceiptStatusResponse> getReceiptStatus(UUID id) {
        // TODO: Implement status retrieval logic
        ReceiptStatusResponse response = new ReceiptStatusResponse();
        response.setJobId(id);
        response.setStatus(ReceiptStatusResponse.StatusEnum.PROCESSING);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReceiptResultResponse> getReceiptResults(UUID id) {
        // TODO: Implement results retrieval logic
        ReceiptResultResponse response = new ReceiptResultResponse();
        response.setJobId(id);
        return ResponseEntity.ok(response);
    }
}
