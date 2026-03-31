package com.example.goodsprice.service.receipt;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Receipt entity to track processing status and store results.
 */
@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
public class Receipt {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "image_hash", nullable = false, unique = true)
    private String imageHash;

    @Column(name = "original_filename")
    private String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReceiptStatus status = ReceiptStatus.PENDING;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_location")
    private String storeLocation;

    @Column(name = "receipt_date")
    private String receiptDate;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "extracted_data", columnDefinition = "TEXT")
    private String extractedDataJson;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public Receipt(UUID id, String imageHash, String originalFilename) {
        this.id = id;
        this.imageHash = imageHash;
        this.originalFilename = originalFilename;
        this.status = ReceiptStatus.PENDING;
    }

    public void markAsProcessing() {
        this.status = ReceiptStatus.PROCESSING;
    }

    public void markAsCompleted(Map<String, Object> extractedData, String extractedDataJson) {
        this.status = ReceiptStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();

        // Extract fields from result
        this.storeName = (String) extractedData.get("storeName");
        this.storeLocation = (String) extractedData.get("storeLocation");
        this.receiptDate = (String) extractedData.get("date");

        Object totalAmount = extractedData.get("totalAmount");
        if (totalAmount instanceof Number) {
            this.totalAmount = ((Number) totalAmount).doubleValue();
        }

        // Store full extracted data as JSON (includes items)
        this.extractedDataJson = extractedDataJson;
    }

    public void markAsFailed(String errorMessage) {
        this.status = ReceiptStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Reset receipt for retry processing.
     * Clears previous error and sets status back to PENDING.
     */
    public void resetForRetry() {
        this.status = ReceiptStatus.PENDING;
        this.errorMessage = null;
        this.storeName = null;
        this.storeLocation = null;
        this.receiptDate = null;
        this.totalAmount = null;
        this.extractedDataJson = null;
        this.processedAt = null;
    }
}
