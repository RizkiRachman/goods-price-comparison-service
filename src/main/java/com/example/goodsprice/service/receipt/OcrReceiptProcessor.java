package com.example.goodsprice.service.receipt;

import com.example.goodsprice.config.properties.TesseractProperties;
import com.example.goodsprice.service.ocr.OcrProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OCR-based receipt processor using Tesseract.
 * Extracts raw text from receipt images.
 * Only created when ocr.tesseract.enabled=true.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ocr.tesseract", name = "enabled", havingValue = "true")
public class OcrReceiptProcessor implements ReceiptProcessor {

    private final OcrProvider ocrProvider;
    private final TesseractProperties tesseractProperties;

    @Override
    public Map<String, Object> processReceipt(MultipartFile image) {
        log.info("Processing receipt with OCR (Tesseract): {}", image.getOriginalFilename());

        if (!isAvailable()) {
            throw new IllegalStateException("OCR processor is not available. Check Tesseract configuration.");
        }

        try {
            // Convert image to base64
            String base64Image = convertToBase64(image);

            // Extract text using OCR
            String extractedText = ocrProvider.extractText(base64Image);

            // Build result
            Map<String, Object> result = new HashMap<>();
            result.put("receiptId", UUID.randomUUID().toString());
            result.put("status", "COMPLETED");
            result.put("originalFilename", image.getOriginalFilename());
            result.put("processor", getProcessorName());
            result.put("rawText", extractedText);
            result.put("storeName", null); // OCR doesn't parse structured data
            result.put("date", null);
            result.put("items", null);
            result.put("totalAmount", null);

            log.info("Receipt processed with OCR successfully: {}", result.get("receiptId"));

            return result;

        } catch (Exception e) {
            log.error("Failed to process receipt with OCR: {}", e.getMessage(), e);
            throw new RuntimeException("Receipt processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProcessorName() {
        return "ocr";
    }

    @Override
    public boolean isAvailable() {
        return ocrProvider.isAvailable() && tesseractProperties.isAvailable();
    }

    /**
     * Convert MultipartFile to base64 string.
     */
    private String convertToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
