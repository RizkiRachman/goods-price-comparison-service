package com.example.goodsprice.llm.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
