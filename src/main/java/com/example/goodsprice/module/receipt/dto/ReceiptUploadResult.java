package com.example.goodsprice.module.receipt.dto;

import com.example.goodsprice.module.receipt.entity.ReceiptStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Result of receipt upload request.
 */
@Data
@Builder
public class ReceiptUploadResult {
    private UUID receiptId;
    private ReceiptStatus status;
    private String message;
    private boolean isDuplicate;
}
