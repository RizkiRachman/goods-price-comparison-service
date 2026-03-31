package com.example.goodsprice.service.llm;

import com.example.goodsprice.config.properties.LlmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LLM Provider Factory.
 * Creates the appropriate LLM provider bean based on configuration.
 */
@Slf4j
@Configuration
public class LlmProviderFactory {

    /**
     * Creates the Gemini provider bean.
     */
    @Bean
    public GeminiLLMProvider geminiLLMProvider(LlmProperties llmProperties) {
        return new GeminiLLMProvider(llmProperties);
    }

    /**
     * Creates the Local (Ollama) provider bean.
     */
    @Bean
    public LocalLLMProvider localLLMProvider(LlmProperties llmProperties) {
        return new LocalLLMProvider(llmProperties);
    }

    /**
     * Creates the LLM provider bean based on configured provider.
     * Supports: local (Ollama), gemini (Google)
     *
     * @param llmProperties LLM configuration properties
     * @param geminiProvider Gemini provider bean
     * @param localProvider Local provider bean
     * @return Active LLM provider
     */
    @Bean
    public LLMProvider llmProvider(LlmProperties llmProperties,
                                   GeminiLLMProvider geminiProvider,
                                   LocalLLMProvider localProvider) {
        String provider = llmProperties.getProvider();
        
        // Handle null or empty provider
        if (provider == null || provider.isEmpty()) {
            log.warn("No LLM provider configured, falling back to local");
            return localProvider;
        }

        log.info("Configuring LLM provider: {}", provider);

        return switch (provider.toLowerCase()) {
            case "gemini" -> geminiProvider;
            case "local" -> localProvider;
            // TODO: case "openai" -> new OpenAiLLMProvider(llmProperties);
            // TODO: case "anthropic" -> new AnthropicLLMProvider(llmProperties);
            default -> {
                log.warn("Unknown provider '{}', falling back to local", provider);
                yield localProvider;
            }
        };
    }
}
