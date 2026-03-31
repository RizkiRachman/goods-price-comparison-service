package com.example.goodsprice.service.receipt;

import com.example.goodsprice.service.llm.LLMProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * LLM-based receipt processor using Gemini.
 * Extracts structured data from receipt images.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LlmReceiptProcessor implements ReceiptProcessor {

    private final LLMProvider llmProvider;

    @Override
    public Map<String, Object> processReceipt(MultipartFile image) {
        log.info("Processing receipt with LLM (Gemini): {}", image.getOriginalFilename());

        if (!isAvailable()) {
            throw new IllegalStateException("LLM processor is not available. Check API key configuration.");
        }

        try {
            // Convert image to base64
            String base64Image = convertToBase64(image);

            // Extract data using LLM (returns structured data)
            Map<String, Object> extractedData = llmProvider.extractReceiptData(base64Image);

            // Add metadata
            extractedData.put("receiptId", UUID.randomUUID().toString());
            extractedData.put("status", "COMPLETED");
            extractedData.put("originalFilename", image.getOriginalFilename());
            extractedData.put("processor", getProcessorName());

            log.info("Receipt processed with LLM successfully: {}", extractedData.get("receiptId"));

            return extractedData;

        } catch (Exception e) {
            log.error("Failed to process receipt with LLM: {}", e.getMessage(), e);
            throw new RuntimeException("Receipt processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProcessorName() {
        return "llm";
    }

    @Override
    public boolean isAvailable() {
        return llmProvider.isAvailable();
    }

    /**
     * Convert MultipartFile to base64 string.
     */
    private String convertToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
