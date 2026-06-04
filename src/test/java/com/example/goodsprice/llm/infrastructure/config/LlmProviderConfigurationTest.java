package com.example.goodsprice.llm.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import com.example.goodsprice.llm.infrastructure.adapter.provider.LocalLlmProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LlmProviderConfigurationTest {

  @Autowired private ApplicationContext context;

  @Test
  @DisplayName("Should create RestTemplate bean")
  void shouldCreateRestTemplateBean() {
    assertNotNull(context.getBean("restTemplate"));
  }

  @Test
  @DisplayName("Should create LocalLlmProvider bean by name")
  void shouldCreateLocalLlmProviderBean() {
    assertNotNull(context.getBean("localLlmProvider"));
  }

  @Test
  @DisplayName("Should create GeminiLlmProvider bean by name")
  void shouldCreateGeminiLlmProviderBean() {
    assertNotNull(context.getBean("geminiLlmProvider"));
  }

  @Test
  @DisplayName("Should create GroqLlmProvider bean by name")
  void shouldCreateGroqLlmProviderBean() {
    assertNotNull(context.getBean("groqLlmProvider"));
  }

  @Test
  @DisplayName("Should create SumopodLlmProvider bean by name")
  void shouldCreateSumopodLlmProviderBean() {
    assertNotNull(context.getBean("sumopodLlmProvider"));
  }

  @Test
  @DisplayName("Should select correct LlmProviderPort based on test profile (local)")
  void shouldSelectCorrectLlmProviderPort() {
    var provider = context.getBean("llmProvider", LlmProviderPort.class);
    assertNotNull(provider);
    assertTrue(provider instanceof LocalLlmProvider);
  }

  @Test
  @DisplayName("Should have LocalLlmProvider as the active provider when provider is local")
  void shouldHaveLocalProviderAsActive() {
    var provider = context.getBean("llmProvider", LlmProviderPort.class);
    assertNotNull(provider);
    assertTrue(provider.isAvailable());
    assertEquals("local", provider.getProviderName());
  }
}
