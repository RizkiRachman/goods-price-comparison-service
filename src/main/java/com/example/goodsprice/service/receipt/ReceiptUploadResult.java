package com.example.goodsprice.service.receipt;

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
