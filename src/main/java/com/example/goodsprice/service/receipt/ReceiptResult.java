package com.example.goodsprice.service.receipt;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Result of receipt processing query.
 */
@Data
@Builder
public class ReceiptResult {
    private UUID receiptId;
    private ReceiptStatus status;
    private String storeName;
    private String storeLocation;
    private String date;
    private Double totalAmount;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
