package com.example.goodsprice.service.llm;

import com.example.goodsprice.config.properties.LlmProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for LLM operations.
 * Delegates to the configured LLM provider.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {

    private final LLMProvider llmProvider;
    private final LlmProperties llmProperties;

    /**
     * Extract receipt data from image using configured LLM.
     *
     * @param imageBase64 Base64 encoded image
     * @return Extracted receipt data
     */
    public Map<String, Object> extractReceipt(String imageBase64) {
        log.info("Extracting receipt data using {} provider", llmProvider.getProviderName());
        
        if (!llmProvider.isAvailable()) {
            throw new IllegalStateException("LLM provider is not available: " + llmProvider.getProviderName());
        }
        
        return llmProvider.extractReceiptData(imageBase64);
    }

    /**
     * Get current active provider name.
     *
     * @return Provider name
     */
    public String getCurrentProvider() {
        return llmProperties.getProvider();
    }
}
