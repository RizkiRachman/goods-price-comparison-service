package com.example.goodsprice.receipt.infrastructure.adapter.event;

import java.util.UUID;

public record ReceiptUploadedEvent(UUID receiptId, String imageHash, String originalFilename) {}
