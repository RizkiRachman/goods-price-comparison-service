package com.example.goodsprice.module.llm;

import java.util.Map;

/**
 * Interface for LLM providers.
 * Implementations: LocalLLMProvider, OpenAILLMProvider, AnthropicLLMProvider
 */
public interface LLMProvider {

    /**
     * Extract structured data from a receipt image.
     *
     * @param imageBase64 Base64 encoded image data
     * @return Extracted receipt data as Map
     */
    Map<String, Object> extractReceiptData(String imageBase64);

    /**
     * Get the provider name.
     *
     * @return Provider identifier (local, openai, anthropic)
     */
    String getProviderName();

    /**
     * Check if provider is available/healthy.
     *
     * @return true if provider is ready
     */
    boolean isAvailable();
}
