package com.example.goodsprice.config;

import com.example.goodsprice.config.properties.LlmProperties;
import com.example.goodsprice.service.llm.LLMProvider;
import com.example.goodsprice.service.llm.LocalLLMProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LLM Configuration.
 * Creates the appropriate LLM provider bean based on configuration.
 */
@Slf4j
@Configuration
public class LLMConfiguration {

    /**
     * Creates the LLM provider bean based on configured provider.
     * Currently supports: local (Ollama)
     * TODO: Add OpenAI and Anthropic providers
     *
     * @param llmProperties LLM configuration properties
     * @return Active LLM provider
     */
    @Bean
    public LLMProvider llmProvider(LlmProperties llmProperties) {
        String provider = llmProperties.getProvider();

        log.info("Configuring LLM provider: {}", provider);

        return switch (provider.toLowerCase()) {
            case "local" -> new LocalLLMProvider(llmProperties);
            // TODO: case "openai" -> new OpenAiLLMProvider(llmProperties);
            // TODO: case "anthropic" -> new AnthropicLLMProvider(llmProperties);
            default -> {
                log.warn("Unknown provider '{}', falling back to local", provider);
                yield new LocalLLMProvider(llmProperties);
            }
        };
    }
}
