package com.example.goodsprice.module.receipt.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event fired when a receipt needs to be processed.
 * Contains receipt ID and image bytes for async processing.
 */
@Getter
public class ReceiptProcessEvent extends ApplicationEvent {

    private final UUID receiptId;
    private final byte[] imageBytes;
    private final String originalFilename;

    public ReceiptProcessEvent(Object source, UUID receiptId, byte[] imageBytes, String originalFilename) {
        super(source);
        this.receiptId = receiptId;
        this.imageBytes = imageBytes;
        this.originalFilename = originalFilename;
    }
}
