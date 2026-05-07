package com.example.goodsprice.llm.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LlmServiceTest {

  @Autowired private LlmService llmService;

  @Autowired private LlmProviderPort llmProvider;

  @Test
  @DisplayName("Should have LLM provider bean configured")
  void shouldHaveLLMProvider() {
    assertNotNull(llmProvider);
    assertNotNull(llmProvider.getProviderName());
  }

  @Test
  @DisplayName("Should have LLM service bean configured")
  void shouldHaveLLMService() {
    assertNotNull(llmService);
    assertEquals("local", llmService.getCurrentProvider());
  }

  @Test
  @DisplayName("Should check provider availability")
  void shouldCheckProviderAvailability() {
    assertDoesNotThrow(() -> llmProvider.isAvailable());
  }

  @Test
  @DisplayName("Should extract receipt data structure")
  void shouldExtractReceiptDataStructure() {
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
