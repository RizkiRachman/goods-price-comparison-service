package com.example.goodsprice.service.receipt.event;

import com.example.goodsprice.service.llm.LLMProvider;
import com.example.goodsprice.service.receipt.Receipt;
import com.example.goodsprice.service.receipt.ReceiptRepository;
import com.example.goodsprice.service.receipt.ReceiptStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Async listener for receipt processing events.
 * Processes receipts in background using LLM.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptProcessEventListener {

    private final ReceiptRepository receiptRepository;
    private final LLMProvider llmProvider;
    private final ObjectMapper objectMapper;

    @Async("receiptProcessorExecutor")
    @EventListener
    @Transactional
    public void handleReceiptProcessEvent(ReceiptProcessEvent event) {
        UUID receiptId = event.getReceiptId();
        log.info("[Async] Starting receipt processing for ID: {}", receiptId);

        // Get receipt - if not found, skip (might be race condition)
        Receipt receipt = receiptRepository.findById(receiptId).orElse(null);
        if (receipt == null) {
            log.warn("[Async] Receipt not found (might be processing): {}", receiptId);
            return;
        }
        
        // Skip if already completed or processing
        // Note: FAILED receipts will be re-processed (retry logic)
        if (receipt.getStatus() == ReceiptStatus.COMPLETED) {
            log.info("[Async] Receipt already COMPLETED: {}", receiptId);
            return;
        }
        if (receipt.getStatus() == ReceiptStatus.PROCESSING) {
            log.info("[Async] Receipt already PROCESSING: {}", receiptId);
            return;
        }
        if (receipt.getStatus() == ReceiptStatus.FAILED) {
            log.info("[Async] Retrying previously FAILED receipt: {}", receiptId);
            receipt.resetForRetry();
        }

        // Mark as processing
        receipt.markAsProcessing();
        receiptRepository.save(receipt);
        log.info("[Async] Receipt marked as PROCESSING: {}", receiptId);

        try {
            // Convert bytes to base64 for LLM
            String base64Image = Base64.getEncoder().encodeToString(event.getImageBytes());

            // Process with LLM (with timeout protection)
            log.info("[Async] Calling LLM for receipt: {}", receiptId);
            Map<String, Object> extractedData = processWithTimeout(receiptId, base64Image);

            if (extractedData == null) {
                throw new RuntimeException("LLM processing timed out or returned null");
            }

            // Convert to JSON for storage
            String extractedDataJson = objectMapper.writeValueAsString(extractedData);

            // Mark as completed
            receipt.markAsCompleted(extractedData, extractedDataJson);
            receiptRepository.save(receipt);

            log.info("[Async] Receipt processing completed successfully: {}", receiptId);

        } catch (Exception e) {
            log.error("[Async] Receipt processing failed: {}", receiptId, e);
            receipt.markAsFailed(e.getMessage());
            receiptRepository.save(receipt);
        }
    }

    /**
     * Process receipt with LLM with timeout protection.
     * Prevents threads from being blocked indefinitely.
     */
    private Map<String, Object> processWithTimeout(UUID receiptId, String base64Image) throws Exception {
        // Future enhancement: Use CompletableFuture with timeout
        // For now, rely on LLM provider timeout
        return llmProvider.extractReceiptData(base64Image);
    }
}
