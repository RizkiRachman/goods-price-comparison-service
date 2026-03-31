package com.example.goodsprice.service.llm;

import com.example.goodsprice.config.properties.LlmProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Local LLM provider using Ollama.
 * Note: Not annotated with @Component - bean is created via LLMConfiguration
 */
@Slf4j
@RequiredArgsConstructor
public class LocalLLMProvider implements LLMProvider {

    private final LlmProperties llmProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Map<String, Object> extractReceiptData(String imageBase64) {
        log.debug("Extracting receipt using local Ollama model");
        
        // TODO: Implement actual Ollama API call
        // For now, return mock data
        Map<String, Object> result = new HashMap<>();
        result.put("store", "Mock Store");
        result.put("date", "2024-01-01");
        result.put("items", List.of(
            Map.of("name", "Milk", "price", 5000, "quantity", 1),
            Map.of("name", "Bread", "price", 3000, "quantity", 2)
        ));
        result.put("total", 11000);
        
        return result;
    }

    @Override
    public String getProviderName() {
        return "local";
    }

    @Override
    public boolean isAvailable() {
        // Check if provider type is configured as local
        if (!llmProperties.getLocal().isLocal()) {
            log.warn("Local provider is not configured as local type");
            return false;
        }
        
        // TODO: Implement actual health check to Ollama
        return true;
    }
}
