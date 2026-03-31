package com.example.goodsprice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OCR Service configuration properties.
 * Controls which receipt processing method to use.
 */
@Data
@ConfigurationProperties(prefix = "ocr")
public class OcrServiceProperties {

    /**
     * OCR Provider Selection
     * Options: ocr (default), llm, or hybrid
     */
    private String provider = "ocr";

    /**
     * Timeout in seconds for OCR processing
     */
    private int timeoutSeconds = 30;

    /**
     * Maximum file size in MB
     */
    private int maxFileSizeMb = 10;

    /**
     * Allowed image formats (comma-separated)
     */
    private String allowedFormats = "jpg,jpeg,png,webp";

    /**
     * Check if OCR (Tesseract) is the selected provider
     */
    public boolean isOcrProvider() {
        return "ocr".equalsIgnoreCase(provider) || "tesseract".equalsIgnoreCase(provider);
    }

    /**
     * Check if LLM is the selected provider
     */
    public boolean isLlmProvider() {
        return "llm".equalsIgnoreCase(provider) || "gemini".equalsIgnoreCase(provider);
    }

    /**
     * Check if hybrid mode is selected (uses both)
     */
    public boolean isHybridProvider() {
        return "hybrid".equalsIgnoreCase(provider) || "both".equalsIgnoreCase(provider);
    }
}
