package com.example.goodsprice.service.receipt;

import com.example.goodsprice.service.llm.LLMProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * Service for receipt processing and OCR extraction.
 * Integrates directly with LLM provider (Gemini) for image analysis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final LLMProvider llmProvider;

    /**
     * Process receipt image and extract structured data.
     *
     * @param image MultipartFile containing receipt image
     * @return Extracted receipt data
     */
    public Map<String, Object> processReceipt(MultipartFile image) {
        log.info("Processing receipt image: {}", image.getOriginalFilename());

        try {
            // Convert image to base64
            String base64Image = convertToBase64(image);

            // Extract data using LLM (Gemini)
            Map<String, Object> extractedData = llmProvider.extractReceiptData(base64Image);

            // Add metadata
            extractedData.put("receiptId", UUID.randomUUID().toString());
            extractedData.put("status", "COMPLETED");
            extractedData.put("originalFilename", image.getOriginalFilename());

            log.info("Receipt processed successfully: {}", extractedData.get("receiptId"));

            return extractedData;

        } catch (Exception e) {
            log.error("Failed to process receipt: {}", e.getMessage(), e);
            throw new RuntimeException("Receipt processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Convert MultipartFile to base64 string.
     */
    private String convertToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
