package com.example.goodsprice.llm.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import com.example.goodsprice.llm.infrastructure.adapter.provider.GeminiLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.GroqLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.LocalLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.SumopodLlmProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ActiveProfiles("test")
class LlmProviderConfigurationTest {

  @Autowired private ApplicationContext context;
  @Autowired private LlmProviderConfiguration configuration;

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

  @Test
  @DisplayName("Should create RestTemplate bean with timeouts")
  void shouldCreateRestTemplateBeanWithTimeouts() {
    RestTemplate restTemplate = configuration.restTemplate();
    assertNotNull(restTemplate);
    assertNotNull(restTemplate.getRequestFactory());
  }

  @Nested
  @SpringBootTest(properties = {"llm.provider=gemini"})
  @ActiveProfiles("test")
  @DisplayName("When provider is GEMINI")
  class GeminiProviderTest {

    @Autowired private ApplicationContext context;

    @Test
    @DisplayName("Should select GeminiLlmProvider as llmProvider")
    void shouldSelectGeminiProvider() {
      var provider = context.getBean("llmProvider", LlmProviderPort.class);
      assertTrue(provider instanceof GeminiLlmProvider);
    }
  }

  @Nested
  @SpringBootTest(properties = {"llm.provider=groq"})
  @ActiveProfiles("test")
  @DisplayName("When provider is GROQ")
  class GroqProviderTest {

    @Autowired private ApplicationContext context;

    @Test
    @DisplayName("Should select GroqLlmProvider as llmProvider")
    void shouldSelectGroqProvider() {
      var provider = context.getBean("llmProvider", LlmProviderPort.class);
      assertTrue(provider instanceof GroqLlmProvider);
    }
  }

  @Nested
  @SpringBootTest(properties = {"llm.provider=sumopod"})
  @ActiveProfiles("test")
  @DisplayName("When provider is SUMOPOD")
  class SumopodProviderTest {

    @Autowired private ApplicationContext context;

    @Test
    @DisplayName("Should select SumopodLlmProvider as llmProvider")
    void shouldSelectSumopodProvider() {
      var provider = context.getBean("llmProvider", LlmProviderPort.class);
      assertTrue(provider instanceof SumopodLlmProvider);
    }
  }
}
