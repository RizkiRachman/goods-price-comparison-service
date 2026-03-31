package com.example.goodsprice.service.receipt;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Receipt Processor interface.
 * Implementations process receipt images and extract structured data.
 */
public interface ReceiptProcessor {

    /**
     * Process receipt image and extract structured data.
     *
     * @param image MultipartFile containing receipt image
     * @return Extracted receipt data
     */
    Map<String, Object> processReceipt(MultipartFile image);

    /**
     * Get the processor name/identifier.
     *
     * @return Processor name
     */
    String getProcessorName();

    /**
     * Check if this processor is available and properly configured.
     *
     * @return true if available
     */
    boolean isAvailable();
}
