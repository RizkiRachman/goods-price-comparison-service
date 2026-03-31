package com.example.goodsprice.service.ocr;

/**
 * OCR Provider interface for text extraction from images.
 * Implementations: TesseractOcrProvider
 */
public interface OcrProvider {

    /**
     * Extract text from an image.
     *
     * @param imageBase64 Base64 encoded image
     * @return Extracted text
     */
    String extractText(String imageBase64);

    /**
     * Check if this provider is available and properly configured.
     *
     * @return true if available
     */
    boolean isAvailable();

    /**
     * Get the provider name.
     *
     * @return Provider identifier
     */
    String getProviderName();
}
