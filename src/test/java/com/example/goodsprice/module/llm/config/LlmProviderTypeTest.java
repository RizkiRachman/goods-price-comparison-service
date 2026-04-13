package com.example.goodsprice.module.llm.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LLM Provider type configuration.
 */
@SpringBootTest
@ActiveProfiles("test")
class LlmProviderTypeTest {

    @Autowired
    private LlmProperties llmProperties;

    @Test
    @DisplayName("Should load provider type from properties")
    void shouldLoadProviderTypeFromProperties() {
        // Given - properties are loaded
        
        // When - get provider configs
        LlmProperties.ProviderConfig localConfig = llmProperties.getLocal();
        LlmProperties.ProviderConfig geminiConfig = llmProperties.getGemini();
        LlmProperties.ProviderConfig openaiConfig = llmProperties.getOpenai();
        LlmProperties.ProviderConfig anthropicConfig = llmProperties.getAnthropic();
        
        // Then - verify types
        assertEquals("local", localConfig.getType());
        assertEquals("cloud", geminiConfig.getType());
        assertEquals("cloud", openaiConfig.getType());
        assertEquals("cloud", anthropicConfig.getType());
    }

    @Test
    @DisplayName("Should correctly identify local provider")
    void shouldCorrectlyIdentifyLocalProvider() {
        // Given
        LlmProperties.ProviderConfig localConfig = llmProperties.getLocal();
        
        // When & Then
        assertTrue(localConfig.isLocal());
        assertFalse(localConfig.isCloud());
    }

    @Test
    @DisplayName("Should correctly identify cloud providers")
    void shouldCorrectlyIdentifyCloudProviders() {
        // Given
        LlmProperties.ProviderConfig geminiConfig = llmProperties.getGemini();
        LlmProperties.ProviderConfig openaiConfig = llmProperties.getOpenai();
        
        // When & Then
        assertTrue(geminiConfig.isCloud());
        assertFalse(geminiConfig.isLocal());
        
        assertTrue(openaiConfig.isCloud());
        assertFalse(openaiConfig.isLocal());
    }

    @Test
    @DisplayName("Should get active provider with correct type")
    void shouldGetActiveProviderWithCorrectType() {
        // Given - check what provider is actually configured
        String configuredProvider = llmProperties.getProvider();
        System.out.println("Configured provider: " + configuredProvider);
        
        // When
        LlmProperties.ProviderConfig activeProvider = llmProperties.getActiveProvider();
        String activeType = activeProvider.getType();
        System.out.println("Active provider type: " + activeType);
        
        // Then - verify the active provider matches the configuration
        assertNotNull(activeProvider);
        
        // The type should match the provider configuration
        if ("gemini".equals(configuredProvider) || "openai".equals(configuredProvider) || 
            "anthropic".equals(configuredProvider)) {
            assertTrue(activeProvider.isCloud(), 
                "Provider " + configuredProvider + " should be cloud type");
        } else if ("local".equals(configuredProvider)) {
            assertTrue(activeProvider.isLocal(), 
                "Provider " + configuredProvider + " should be local type");
        }
    }

    @Test
    @DisplayName("Should handle default type as cloud")
    void shouldHandleDefaultTypeAsCloud() {
        // Given - new provider config without explicit type
        LlmProperties.ProviderConfig newConfig = new LlmProperties.ProviderConfig();
        
        // When & Then
        assertEquals("cloud", newConfig.getType());
        assertTrue(newConfig.isCloud());
        assertFalse(newConfig.isLocal());
    }
}
