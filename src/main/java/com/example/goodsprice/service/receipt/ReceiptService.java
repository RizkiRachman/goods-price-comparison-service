package com.example.goodsprice.service.receipt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for receipt processing with async workflow.
 * 
 * Flow:
 * 1. Upload image → Calculate hash
 * 2. Check if already exists (deduplication)
 * 3. Create receipt record with PENDING status
 * 4. Fire async event for processing
 * 5. Return immediately with receipt ID
 * 6. Background worker processes with LLM
 * 7. Update status when complete
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ObjectMapper objectMapper;

    /**
     * Upload receipt for async processing.
     * Returns immediately with receipt ID. Processing happens in background.
     *
     * @param image MultipartFile containing receipt image
     * @return ReceiptUploadResult with ID and status
     */
    @Transactional
    public ReceiptUploadData uploadReceipt(MultipartFile image) {
        log.info("Uploading receipt: {}", image.getOriginalFilename());

        try {
            byte[] imageBytes = image.getBytes();
            String imageHash = calculateSha256Hash(imageBytes);

            // Check if already uploaded (deduplication)
            Optional<Receipt> existingReceipt = receiptRepository.findByImageHash(imageHash);
            if (existingReceipt.isPresent()) {
                Receipt receipt = existingReceipt.get();
                
                // If previous attempt failed, allow retry by deleting old record
                if (receipt.getStatus() == ReceiptStatus.FAILED) {
                    log.info("Receipt previously failed with ID: {}. Allowing retry.", receipt.getId());
                    receiptRepository.delete(receipt);
                    // Continue to create new receipt
                } else {
                    // For COMPLETED or PROCESSING, return existing
                    log.info("Receipt already exists with ID: {} (status: {})",
                            receipt.getId(), receipt.getStatus());

                    return ReceiptUploadData.builder()
                            .receiptId(receipt.getId())
                            .imageHash(imageHash)
                            .imageBytes(null) // No need to return bytes for duplicate
                            .originalFilename(image.getOriginalFilename())
                            .status(receipt.getStatus())
                            .isDuplicate(true)
                            .message("Receipt already uploaded. Current status: " + receipt.getStatus())
                            .build();
                }
            }

            // Create new receipt record
            UUID receiptId = UUID.randomUUID();
            Receipt receipt = new Receipt(receiptId, imageHash, image.getOriginalFilename());
            receiptRepository.save(receipt);

            log.info("Receipt uploaded with ID: {}. Ready for processing.", receiptId);

            // Return data needed for async processing
            // Event will be fired AFTER transaction commits (in controller)
            return ReceiptUploadData.builder()
                    .receiptId(receiptId)
                    .imageHash(imageHash)
                    .imageBytes(imageBytes)
                    .originalFilename(image.getOriginalFilename())
                    .status(ReceiptStatus.PENDING)
                    .isDuplicate(false)
                    .message("Receipt uploaded successfully. Processing in background.")
                    .build();

        } catch (IOException e) {
            log.error("Failed to upload receipt: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload receipt: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get receipt status and result by ID.
     */
    public ReceiptResult getReceiptResult(UUID receiptId) {
        if (receiptId == null) {
            return ReceiptResult.builder()
                    .receiptId(null)
                    .status(null)
                    .errorMessage("Receipt ID cannot be null")
                    .build();
        }

        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);

        if (receiptOpt.isEmpty()) {
            return ReceiptResult.builder()
                    .receiptId(receiptId)
                    .status(null)
                    .errorMessage("Receipt not found")
                    .build();
        }

        Receipt receipt = receiptOpt.get();

        // Parse items from stored JSON if available
        List<Map<String, Object>> items = parseItemsFromJson(receipt.getExtractedDataJson());

        return ReceiptResult.builder()
                .receiptId(receiptId)
                .status(receipt.getStatus())
                .storeName(receipt.getStoreName())
                .storeLocation(receipt.getStoreLocation())
                .date(receipt.getReceiptDate())
                .totalAmount(receipt.getTotalAmount())
                .items(items)
                .errorMessage(receipt.getErrorMessage())
                .createdAt(receipt.getCreatedAt())
                .processedAt(receipt.getProcessedAt())
                .extractedDataJson(receipt.getExtractedDataJson())
                .build();
    }

    /**
     * Parse items list from extracted data JSON.
     */
    private List<Map<String, Object>> parseItemsFromJson(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            Map<String, Object> data = objectMapper.readValue(json, new TypeReference<>() {});
            Object itemsObj = data.get("items");
            if (itemsObj instanceof List) {
                //noinspection unchecked
                return (List<Map<String, Object>>) itemsObj;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.warn("Failed to parse items from JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Calculate SHA-256 hash of image bytes for deduplication.
     */
    private String calculateSha256Hash(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
