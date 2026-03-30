package com.example.goodsprice.config.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LLM Properties configuration binding.
 */
@SpringBootTest
class LlmPropertiesTest {

    @Autowired
    private LlmProperties llmProperties;

    @Test
    @DisplayName("Should load default provider configuration")
    void shouldLoadDefaultProvider() {
        assertEquals("local", llmProperties.getProvider());
    }

    @Test
    @DisplayName("Should load local provider configuration")
    void shouldLoadLocalProviderConfig() {
        LlmProperties.ProviderConfig local = llmProperties.getLocal();
        
        assertNotNull(local);
        assertEquals("http://localhost:11434", local.getBaseUrl());
        assertEquals("llama3.2-vision", local.getModel());
        assertEquals(30, local.getTimeout());
    }

    @Test
    @DisplayName("Should load OpenAI provider configuration")
    void shouldLoadOpenAiConfig() {
        LlmProperties.ProviderConfig openai = llmProperties.getOpenai();
        
        assertNotNull(openai);
        assertEquals("https://api.openai.com/v1", openai.getBaseUrl());
        assertEquals("gpt-4-vision-preview", openai.getModel());
    }

    @Test
    @DisplayName("Should get active provider based on selection")
    void shouldGetActiveProvider() {
        LlmProperties.ProviderConfig active = llmProperties.getActiveProvider();
        
        assertNotNull(active);
        // With default provider=local, should return local config
        assertEquals("http://localhost:11434", active.getBaseUrl());
    }
}
