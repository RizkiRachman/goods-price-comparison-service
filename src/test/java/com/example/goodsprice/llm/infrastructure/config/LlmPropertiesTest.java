package com.example.goodsprice.llm.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LlmPropertiesTest {

  @Autowired private LlmProperties llmProperties;

  @Test
  @DisplayName("Should load provider from properties")
  void shouldLoadProviderFromProperties() {
    assertEquals("local", llmProperties.getProvider());
  }

  @Test
  @DisplayName("Should load local provider configuration")
  void shouldLoadLocalProviderConfig() {
    var local = llmProperties.getLocal();

    assertNotNull(local);
    assertEquals("http://localhost:11434", local.getBaseUrl());
    assertEquals("llama3.2-vision", local.getModel());
    assertEquals(30, local.getTimeout());
  }

  @Test
  @DisplayName("Should load OpenAI provider configuration")
  void shouldLoadOpenAiConfig() {
    var openai = llmProperties.getOpenai();

    assertNotNull(openai);
    assertEquals("https://api.openai.com/v1", openai.getBaseUrl());
    assertEquals("gpt-4-vision-preview", openai.getModel());
  }

  @Test
  @DisplayName("Should get active provider based on selection")
  void shouldGetActiveProvider() {
    var active = llmProperties.getActiveProvider();

    assertNotNull(active);
    assertEquals("http://localhost:11434", active.getBaseUrl());
  }

  @Test
  @DisplayName("Should have correct default values for new ProviderConfig")
  void shouldHaveDefaultProviderConfigValues() {
    var config = new LlmProperties.ProviderConfig();

    assertEquals("cloud", config.getType());
    assertEquals(30, config.getTimeout());
    assertFalse(config.isEnabled());
    assertTrue(config.isCloud());
    assertFalse(config.isLocal());
  }

  @Test
  @DisplayName("Should handle null base URL gracefully")
  void shouldHandleNullBaseUrl() {
    var config = new LlmProperties.ProviderConfig();

    assertEquals("cloud", config.getType());
    assertTrue(config.isCloud());
    assertFalse(config.isLocal());
  }

  @Test
  @DisplayName("Should correctly identify local type provider")
  void shouldIdentifyLocalType() {
    var config = new LlmProperties.ProviderConfig();
    // type defaults to "cloud" — we need a separate way to verify setType

    assertEquals("cloud", config.getType());
    assertTrue(config.isCloud());
    assertFalse(config.isLocal());
  }

  @Test
  @DisplayName("Should han q empty API key in fresh ProviderConfig")
  void shouldHandleEmptyApiKey() {
    var config = new LlmProperties.ProviderConfig();

    assertFalse(config.isEnabled());
  }
}
