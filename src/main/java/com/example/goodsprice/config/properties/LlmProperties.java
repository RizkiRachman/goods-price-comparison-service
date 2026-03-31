package com.example.goodsprice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LLM Provider configuration properties.
 * Maps to llm.* properties in config/llm.properties
 */
@Data
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

    /**
     * Selected provider: local | openai | anthropic
     */
    private String provider = "local";

    /**
     * Local LLM configuration (Ollama)
     */
    private ProviderConfig local = new ProviderConfig();

    /**
     * OpenAI configuration
     */
    private ProviderConfig openai = new ProviderConfig();

    /**
     * Anthropic configuration
     */
    private ProviderConfig anthropic = new ProviderConfig();

    /**
     * Google Gemini configuration
     */
    private ProviderConfig gemini = new ProviderConfig();

    /**
     * Gets the active provider configuration
     */
    public ProviderConfig getActiveProvider() {
        return switch (provider.toLowerCase()) {
            case "openai" -> openai;
            case "anthropic" -> anthropic;
            case "gemini" -> gemini;
            default -> local;
        };
    }

    /**
     * Provider-specific configuration
     */
    @Data
    public static class ProviderConfig {
        private String baseUrl;
        private String model;
        private int timeout = 30;
        private String apiKey;
        private boolean enabled = false;
    }
}
