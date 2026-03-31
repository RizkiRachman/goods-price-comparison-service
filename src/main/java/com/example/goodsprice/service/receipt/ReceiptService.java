package com.example.goodsprice.service.receipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Service for receipt processing.
 * Delegates to configured ReceiptProcessor (OCR or LLM).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptProcessor receiptProcessor;

    /**
     * Process receipt image and extract structured data.
     * Uses configured processor (OCR by default, falls back to LLM).
     *
     * @param image MultipartFile containing receipt image
     * @return Extracted receipt data
     */
    public Map<String, Object> processReceipt(MultipartFile image) {
        log.info("Processing receipt image: {} using {}", 
                image.getOriginalFilename(), 
                receiptProcessor.getProcessorName());

        try {
            // Delegate to the configured processor
            Map<String, Object> result = receiptProcessor.processReceipt(image);
            
            log.info("Receipt processed successfully: {}", result.get("receiptId"));
            return result;

        } catch (Exception e) {
            log.error("Failed to process receipt: {}", e.getMessage(), e);
            throw new RuntimeException("Receipt processing failed: " + e.getMessage(), e);
        }
    }
}
