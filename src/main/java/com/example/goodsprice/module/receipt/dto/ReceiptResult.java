package com.example.goodsprice.module.receipt.dto;

import com.example.goodsprice.module.receipt.entity.ReceiptStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private List<Map<String, Object>> items; // Receipt items with product details
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String extractedDataJson; // Full extracted data as JSON
}
