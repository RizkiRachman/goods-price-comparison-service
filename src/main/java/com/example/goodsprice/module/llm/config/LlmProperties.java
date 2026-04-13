package com.example.goodsprice.module.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LLM Provider configuration properties.
 * Maps to llm.* properties in config/llm.properties
 */
@Data
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

    /**
     * Selected provider: local | openai | anthropic | gemini
     * Loaded from properties file (core.llm.provider)
     */
    private String provider;

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
     * Gets the active provider configuration based on provider name.
     * Falls back to local if provider is not set or unknown.
     */
    public ProviderConfig getActiveProvider() {
        if (provider == null || provider.isEmpty()) {
            return local;
        }
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
        private String type = "cloud"; // local or cloud
        
        /**
         * Check if this is a local provider (requires local service)
         */
        public boolean isLocal() {
            return "local".equalsIgnoreCase(type);
        }
        
        /**
         * Check if this is a cloud provider (uses API)
         */
        public boolean isCloud() {
            return !isLocal();
        }
    }
}
