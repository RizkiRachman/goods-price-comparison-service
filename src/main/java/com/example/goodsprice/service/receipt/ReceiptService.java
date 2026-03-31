package com.example.goodsprice.service.receipt;

import com.example.goodsprice.service.receipt.event.ReceiptProcessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Upload receipt for async processing.
     * Returns immediately with receipt ID. Processing happens in background.
     *
     * @param image MultipartFile containing receipt image
     * @return ReceiptUploadResult with ID and status
     */
    @Transactional
    public ReceiptUploadResult uploadReceipt(MultipartFile image) {
        log.info("Uploading receipt: {}", image.getOriginalFilename());

        try {
            byte[] imageBytes = image.getBytes();
            String imageHash = calculateSha256Hash(imageBytes);
            
            // Check if already uploaded (deduplication)
            Optional<Receipt> existingReceipt = receiptRepository.findByImageHash(imageHash);
            if (existingReceipt.isPresent()) {
                Receipt receipt = existingReceipt.get();
                log.info("Receipt already exists with ID: {} (status: {})", 
                        receipt.getId(), receipt.getStatus());
                
                return ReceiptUploadResult.builder()
                        .receiptId(receipt.getId())
                        .status(receipt.getStatus())
                        .message("Receipt already uploaded. Current status: " + receipt.getStatus())
                        .isDuplicate(true)
                        .build();
            }
            
            // Create new receipt record
            UUID receiptId = UUID.randomUUID();
            Receipt receipt = new Receipt(receiptId, imageHash, image.getOriginalFilename());
            receiptRepository.save(receipt);
            
            // Fire async event for processing
            eventPublisher.publishEvent(new ReceiptProcessEvent(
                    this, receiptId, imageBytes, image.getOriginalFilename()));
            
            log.info("Receipt uploaded with ID: {}. Processing started in background.", receiptId);
            
            return ReceiptUploadResult.builder()
                    .receiptId(receiptId)
                    .status(ReceiptStatus.PENDING)
                    .message("Receipt uploaded successfully. Processing in background.")
                    .isDuplicate(false)
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
        
        return ReceiptResult.builder()
                .receiptId(receiptId)
                .status(receipt.getStatus())
                .storeName(receipt.getStoreName())
                .storeLocation(receipt.getStoreLocation())
                .date(receipt.getReceiptDate())
                .totalAmount(receipt.getTotalAmount())
                .errorMessage(receipt.getErrorMessage())
                .createdAt(receipt.getCreatedAt())
                .processedAt(receipt.getProcessedAt())
                .build();
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
