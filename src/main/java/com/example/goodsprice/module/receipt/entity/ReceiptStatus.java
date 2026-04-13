package com.example.goodsprice.module.receipt.entity;

import lombok.Getter;

/**
 * Receipt processing status enum.
 */
@Getter
public enum ReceiptStatus {
    PENDING("Pending", "Receipt uploaded, waiting to be processed"),
    PROCESSING("Processing", "Currently extracting data from receipt"),
    COMPLETED("Completed", "Receipt processed successfully"),
    FAILED("Failed", "Failed to process receipt");

    private final String label;
    private final String description;

    ReceiptStatus(String label, String description) {
        this.label = label;
        this.description = description;
    }
}
