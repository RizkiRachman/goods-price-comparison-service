package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GroqLlmProviderTest {

  @Mock private LlmProperties llmProperties;
  @Mock private RestTemplate restTemplate;
  private ObjectMapper objectMapper;

  private GroqLlmProvider provider;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    provider = new GroqLlmProvider(llmProperties, restTemplate, objectMapper);
  }

  @Test
  @DisplayName("Should return correct provider name")
  void shouldReturnProviderName() {
    assertEquals("groq", provider.getProviderName());
  }

  @Test
  @DisplayName("Should return correct API URL")
  void shouldReturnCorrectApiUrl() {
    assertEquals("https://api.groq.com/openai/v1/chat/completions", provider.getApiUrl());
  }

  @Test
  @DisplayName("Should return groq config from LlmProperties")
  void shouldReturnGroqConfig() {
    when(llmProperties.getGroq()).thenReturn(new LlmProperties.ProviderConfig());

    var config = provider.getConfig();

    assertNotNull(config);
  }

  @Test
  @DisplayName("Should not be available when API key is missing")
  void shouldNotBeAvailableWhenApiKeyMissing() {
    var config = new LlmProperties.ProviderConfig();
    config.setApiKey(null);
    config.setEnabled(true);
    when(llmProperties.getGroq()).thenReturn(config);

    assertFalse(provider.isAvailable());
  }

  @Test
  @DisplayName("Should be available when API key is present and enabled")
  void shouldBeAvailableWhenApiKeyPresent() {
    var config = new LlmProperties.ProviderConfig();
    config.setApiKey("test-groq-key");
    config.setEnabled(true);
    when(llmProperties.getGroq()).thenReturn(config);

    assertTrue(provider.isAvailable());
  }

  @Test
  @DisplayName("Should extract receipt data successfully")
  void shouldExtractReceiptData() throws Exception {
    var config = new LlmProperties.ProviderConfig();
    config.setApiKey("test-groq-key");
    config.setEnabled(true);
    config.setModel("test-model");
    when(llmProperties.getGroq()).thenReturn(config);

    String responseJson =
        "{\"choices\": [{\"message\": {\"content\": \"{\\\"storeName\\\": \\\"TOKO MAJU\\\"}\"}}]}";
    Map<String, Object> apiResponse = new ObjectMapper().readValue(responseJson, Map.class);

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

    Map<String, Object> result = provider.extractReceiptData("test-image");

    assertNotNull(result);
    assertEquals("TOKO MAJU", result.get("storeName"));
  }

  @Test
  @DisplayName("Should throw RuntimeException on REST call failure")
  void shouldThrowOnRestFailure() {
    var config = new LlmProperties.ProviderConfig();
    config.setApiKey("test-groq-key");
    config.setEnabled(true);
    config.setModel("test-model");
    when(llmProperties.getGroq()).thenReturn(config);

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenThrow(new RuntimeException("API error"));

    assertThrows(RuntimeException.class, () -> provider.extractReceiptData("test-image"));
  }
}
