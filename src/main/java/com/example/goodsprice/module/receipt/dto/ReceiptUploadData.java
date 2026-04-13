package com.example.goodsprice.module.receipt.dto;

import com.example.goodsprice.module.receipt.entity.ReceiptStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Data returned after receipt upload, includes info needed for async processing.
 */
@Data
@Builder
public class ReceiptUploadData {
    private UUID receiptId;
    private String imageHash;
    private byte[] imageBytes;
    private String originalFilename;
    private ReceiptStatus status;
    private boolean isDuplicate;
    private String message;
}
