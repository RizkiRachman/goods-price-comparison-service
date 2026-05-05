package com.example.goodsprice.receipt.infrastructure.adapter.event;

import java.util.UUID;

public record ReceiptProcessedEvent(UUID receiptId) {}
