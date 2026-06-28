package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.KEY_ERROR;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.KEY_RAW_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeminiLlmProviderTest {

  @Mock private LlmProperties llmProperties;

  private GeminiLlmProvider provider;

  @BeforeEach
  void setUp() {
    provider = new GeminiLlmProvider(llmProperties);
  }

  @Test
  @DisplayName("Should return correct provider name")
  void shouldReturnProviderName() {
    assertEquals("gemini", provider.getProviderName());
  }

  @Test
  @DisplayName("Should not be available when API key is null")
  void shouldNotBeAvailableWhenApiKeyNull() {
    var geminiConfig = new LlmProperties.ProviderConfig();
    geminiConfig.setType("cloud");
    geminiConfig.setApiKey(null);
    doReturn(geminiConfig).when(llmProperties).getGemini();

    assertFalse(provider.isAvailable());
  }

  @Test
  @DisplayName("Should not be available when API key is empty")
  void shouldNotBeAvailableWhenApiKeyEmpty() {
    var geminiConfig = new LlmProperties.ProviderConfig();
    geminiConfig.setType("cloud");
    geminiConfig.setApiKey("");
    doReturn(geminiConfig).when(llmProperties).getGemini();

    assertFalse(provider.isAvailable());
  }

  @Test
  @DisplayName("Should be available when API key is present and type is cloud")
  void shouldBeAvailableWhenApiKeyPresent() {
    var geminiConfig = new LlmProperties.ProviderConfig();
    geminiConfig.setType("cloud");
    geminiConfig.setApiKey("test-gemini-key");
    doReturn(geminiConfig).when(llmProperties).getGemini();

    assertTrue(provider.isAvailable());
  }

  @Test
  @DisplayName("Should not be available when type is local even with API key")
  void shouldNotBeAvailableWhenTypeIsLocal() {
    var geminiConfig = new LlmProperties.ProviderConfig();
    geminiConfig.setType("local");
    geminiConfig.setApiKey("test-gemini-key");
    doReturn(geminiConfig).when(llmProperties).getGemini();

    assertFalse(provider.isAvailable());
  }

  @Test
  @DisplayName("Should throw IllegalStateException when API key is not configured")
  void shouldThrowWhenApiKeyNotConfigured() {
    var geminiConfig = new LlmProperties.ProviderConfig();
    geminiConfig.setType("cloud");
    geminiConfig.setApiKey(null);
    doReturn(geminiConfig).when(llmProperties).getGemini();

    assertThrows(IllegalStateException.class, () -> provider.extractReceiptData("test-image"));
  }

  @Test
  @DisplayName("Should throw RuntimeException when GenAI client fails")
  void shouldThrowWhenGenAiClientFails() {
    var geminiConfig = new LlmProperties.ProviderConfig();
    geminiConfig.setType("cloud");
    geminiConfig.setApiKey("test-gemini-key");
    geminiConfig.setModel("gemini-test-model");
    doReturn(geminiConfig).when(llmProperties).getGemini();

    assertThrows(RuntimeException.class, () -> provider.extractReceiptData("invalid-base64!!"));
  }

  @Test
  @DisplayName("Should return error when parseResponse receives null text")
  void shouldReturnErrorWhenResponseIsNull() {
    var result = provider.parseResponse(null);

    assertNotNull(result);
    assertEquals("Empty response from Gemini", result.get(KEY_ERROR));
  }

  @Test
  @DisplayName("Should return error when parseResponse receives empty text")
  void shouldReturnErrorWhenResponseIsEmpty() {
    var result = provider.parseResponse("");

    assertNotNull(result);
    assertEquals("Empty response from Gemini", result.get(KEY_ERROR));
  }

  @Test
  @DisplayName("Should parse JSON from code-fenced response with json marker")
  void shouldParseJsonBlockFromCodeFencedResponse() {
    var text = "```json\n{\"key\":\"value\"}\n```";
    var result = provider.parseResponse(text);

    assertNotNull(result);
    assertEquals("value", result.get("key"));
  }

  @Test
  @DisplayName("Should parse JSON from plain code-fenced response without json marker")
  void shouldParseJsonBlockFromPlainCodeFencedResponse() {
    var text = "```\n{\"key\":\"value\"}\n```";
    var result = provider.parseResponse(text);

    assertNotNull(result);
    assertEquals("value", result.get("key"));
  }

  @Test
  @DisplayName("Should return raw text when JSON parsing fails on non-JSON response")
  void shouldReturnRawTextWhenJsonParsingFails() {
    var text = "Sorry, I can't process this image";
    var result = provider.parseResponse(text);

    assertNotNull(result);
    assertEquals(text, result.get(KEY_RAW_TEXT));
  }
}
