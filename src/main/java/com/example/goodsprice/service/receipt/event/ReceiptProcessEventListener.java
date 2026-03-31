package com.example.goodsprice.service.receipt.event;

import com.example.goodsprice.service.llm.LLMProvider;
import com.example.goodsprice.service.receipt.Receipt;
import com.example.goodsprice.service.receipt.ReceiptRepository;
import com.example.goodsprice.service.receipt.ReceiptStatus;
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

    @Async("receiptProcessorExecutor")
    @EventListener
    @Transactional
    public void handleReceiptProcessEvent(ReceiptProcessEvent event) {
        UUID receiptId = event.getReceiptId();
        log.info("[Async] Starting receipt processing for ID: {}", receiptId);

        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);
        if (receiptOpt.isEmpty()) {
            log.error("[Async] Receipt not found: {}", receiptId);
            return;
        }

        Receipt receipt = receiptOpt.get();
        
        // Check if already processed (deduplication)
        if (receipt.getStatus() == ReceiptStatus.COMPLETED) {
            log.info("[Async] Receipt already processed: {}", receiptId);
            return;
        }

        // Mark as processing
        receipt.markAsProcessing();
        receiptRepository.save(receipt);
        log.info("[Async] Receipt marked as PROCESSING: {}", receiptId);

        try {
            // Convert bytes to base64 for LLM
            String base64Image = Base64.getEncoder().encodeToString(event.getImageBytes());
            
            // Process with LLM
            log.info("[Async] Calling LLM for receipt: {}", receiptId);
            Map<String, Object> extractedData = llmProvider.extractReceiptData(base64Image);
            
            // Mark as completed
            receipt.markAsCompleted(extractedData);
            receiptRepository.save(receipt);
            
            log.info("[Async] Receipt processing completed successfully: {}", receiptId);
            
        } catch (Exception e) {
            log.error("[Async] Receipt processing failed: {}", receiptId, e);
            receipt.markAsFailed(e.getMessage());
            receiptRepository.save(receipt);
        }
    }
}
