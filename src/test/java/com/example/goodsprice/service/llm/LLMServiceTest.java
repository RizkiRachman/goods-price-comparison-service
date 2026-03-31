package com.example.goodsprice.service.llm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LLM Service and Provider.
 */
@SpringBootTest
class LLMServiceTest {

    @Autowired
    private LLMService llmService;

    @Autowired
    private LLMProvider llmProvider;

    @Test
    @DisplayName("Should have LLM provider bean configured")
    void shouldHaveLLMProvider() {
        assertNotNull(llmProvider);
        // Provider name depends on configuration (gemini in properties)
        assertNotNull(llmProvider.getProviderName());
    }

    @Test
    @DisplayName("Should have LLM service bean configured")
    void shouldHaveLLMService() {
        assertNotNull(llmService);
        // Current provider loaded from properties (gemini)
        assertEquals("gemini", llmService.getCurrentProvider());
    }

    @Test
    @DisplayName("Should check provider availability")
    void shouldCheckProviderAvailability() {
        boolean available = llmProvider.isAvailable();
        // Availability depends on API key configuration
        // Just verify the method runs without error
        assertDoesNotThrow(() -> llmProvider.isAvailable());
    }

    @Test
    @DisplayName("Should extract receipt data structure")
    void shouldExtractReceiptDataStructure() {
        // Skip if provider not available (no API key)
        if (!llmProvider.isAvailable()) {
            System.out.println("Skipping test - provider not available (no API key)");
            return;
        }
        
        String mockImage = "base64encodedimage";
        
        Map<String, Object> result = llmProvider.extractReceiptData(mockImage);
        
        assertNotNull(result);
        assertTrue(result.containsKey("store"));
        assertTrue(result.containsKey("items"));
        assertTrue(result.containsKey("total"));
    }
}
