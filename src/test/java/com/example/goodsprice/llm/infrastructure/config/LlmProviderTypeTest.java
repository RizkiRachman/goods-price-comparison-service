package com.example.goodsprice.llm.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LlmProviderTypeTest {

  @Autowired private LlmProperties llmProperties;

  @Test
  @DisplayName("Should load provider type from properties")
  void shouldLoadProviderTypeFromProperties() {
    var localConfig = llmProperties.getLocal();
    var geminiConfig = llmProperties.getGemini();
    var openaiConfig = llmProperties.getOpenai();
    var anthropicConfig = llmProperties.getAnthropic();

    assertEquals("local", localConfig.getType());
    assertEquals("cloud", geminiConfig.getType());
    assertEquals("cloud", openaiConfig.getType());
    assertEquals("cloud", anthropicConfig.getType());
  }

  @Test
  @DisplayName("Should correctly identify local provider")
  void shouldCorrectlyIdentifyLocalProvider() {
    var localConfig = llmProperties.getLocal();

    assertTrue(localConfig.isLocal());
    assertFalse(localConfig.isCloud());
  }

  @Test
  @DisplayName("Should correctly identify cloud providers")
  void shouldCorrectlyIdentifyCloudProviders() {
    var geminiConfig = llmProperties.getGemini();
    var openaiConfig = llmProperties.getOpenai();

    assertTrue(geminiConfig.isCloud());
    assertFalse(geminiConfig.isLocal());

    assertTrue(openaiConfig.isCloud());
    assertFalse(openaiConfig.isLocal());
  }

  @Test
  @DisplayName("Should get active provider with correct type")
  void shouldGetActiveProviderWithCorrectType() {
    var configuredProvider = llmProperties.getProvider();
    var activeProvider = llmProperties.getActiveProvider();

    assertNotNull(activeProvider);

    if ("gemini".equals(configuredProvider)
        || "openai".equals(configuredProvider)
        || "anthropic".equals(configuredProvider)) {
      assertTrue(activeProvider.isCloud());
    } else if ("local".equals(configuredProvider)) {
      assertTrue(activeProvider.isLocal());
    }
  }

  @Test
  @DisplayName("Should handle default type as cloud")
  void shouldHandleDefaultTypeAsCloud() {
    var newConfig = new LlmProperties.ProviderConfig();

    assertEquals("cloud", newConfig.getType());
    assertTrue(newConfig.isCloud());
    assertFalse(newConfig.isLocal());
  }
}
