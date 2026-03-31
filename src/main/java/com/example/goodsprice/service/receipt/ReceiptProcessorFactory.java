package com.example.goodsprice.service.receipt;

import com.example.goodsprice.config.properties.OcrServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Receipt Processor Factory.
 * Creates the appropriate receipt processor based on configuration.
 * Default: OCR (Tesseract)
 * Fallback: LLM (Gemini)
 */
@Slf4j
@Configuration
public class ReceiptProcessorFactory {

    /**
     * Creates the active receipt processor bean.
     * Priority: OCR (default) → LLM (fallback)
     *
     * @param ocrServiceProperties OCR service configuration
     * @param ocrProcessor OCR-based processor (Tesseract) - optional
     * @param llmProcessor LLM-based processor (Gemini)
     * @return Active receipt processor
     */
    @Bean
    @Primary
    public ReceiptProcessor receiptProcessor(
            OcrServiceProperties ocrServiceProperties,
            org.springframework.beans.factory.ObjectProvider<OcrReceiptProcessor> ocrProcessorProvider,
            LlmReceiptProcessor llmProcessor) {
        
        // Get OCR processor if available
        OcrReceiptProcessor ocrProcessor = ocrProcessorProvider.getIfAvailable();

        String provider = ocrServiceProperties.getProvider();
        log.info("Configuring receipt processor: {}", provider);

        // Check if OCR is requested and available
        if (ocrServiceProperties.isOcrProvider()) {
            if (ocrProcessor != null && ocrProcessor.isAvailable()) {
                log.info("Using OCR (Tesseract) receipt processor");
                return ocrProcessor;
            } else {
                log.warn("OCR processor requested but not available (Tesseract not configured), falling back to LLM");
                if (llmProcessor.isAvailable()) {
                    return llmProcessor;
                }
                log.error("Neither OCR nor LLM processor is available");
                return createFallbackProcessor();
            }
        }

        // Check if LLM is requested
        if (ocrServiceProperties.isLlmProvider()) {
            if (llmProcessor.isAvailable()) {
                log.info("Using LLM (Gemini) receipt processor");
                return llmProcessor;
            } else {
                log.warn("LLM processor requested but not available (API key missing), falling back to OCR");
                if (ocrProcessor != null && ocrProcessor.isAvailable()) {
                    return ocrProcessor;
                }
                log.error("Neither LLM nor OCR processor is available");
                return createFallbackProcessor();
            }
        }

        // Hybrid mode - not implemented yet
        if (ocrServiceProperties.isHybridProvider()) {
            log.warn("Hybrid mode not yet implemented, using LLM");
            if (llmProcessor.isAvailable()) {
                return llmProcessor;
            }
            if (ocrProcessor != null && ocrProcessor.isAvailable()) {
                return ocrProcessor;
            }
            log.error("No receipt processor is available");
            return createFallbackProcessor();
        }

        // Unknown provider - default to OCR
        log.warn("Unknown provider '{}', defaulting to OCR", provider);
        if (ocrProcessor != null && ocrProcessor.isAvailable()) {
            return ocrProcessor;
        }
        if (llmProcessor.isAvailable()) {
            return llmProcessor;
        }

        log.error("No receipt processor is available. Configure Tesseract or LLM API key.");
        return createFallbackProcessor();
    }

    /**
     * Creates a fallback processor that throws exception when used.
     * This allows the application to start even when no processor is configured.
     */
    private ReceiptProcessor createFallbackProcessor() {
        return new ReceiptProcessor() {
            @Override
            public java.util.Map<String, Object> processReceipt(org.springframework.web.multipart.MultipartFile image) {
                throw new IllegalStateException("No receipt processor is available. Configure Tesseract (ocr.tesseract.enabled=true) or LLM API key (GEMINI_API_KEY).");
            }

            @Override
            public String getProcessorName() {
                return "fallback";
            }

            @Override
            public boolean isAvailable() {
                return false;
            }
        };
    }
}
