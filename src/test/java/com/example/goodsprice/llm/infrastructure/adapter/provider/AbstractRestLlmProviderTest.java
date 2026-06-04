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

import com.example.goodsprice.llm.infrastructure.config.LlmConstants;
import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import com.example.goodsprice.llm.infrastructure.config.LlmProperties.ProviderConfig;
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
class AbstractRestLlmProviderTest {

  @Mock private LlmProperties llmProperties;
  @Mock private RestTemplate restTemplate;
  private ObjectMapper objectMapper;

  private ConfigurableRestLlmProvider provider;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    provider = new ConfigurableRestLlmProvider(llmProperties, restTemplate, objectMapper);
  }

  @Test
  @DisplayName("Should return provider name from constant")
  void shouldReturnProviderName() {
    assertEquals("test-provider", provider.getProviderName());
  }

  @Test
  @DisplayName("Should return false when API key is null")
  void shouldReturnFalseWhenApiKeyIsNull() {
    provider.setTestConfig(createConfig(null, false));

    assertFalse(provider.isAvailable());
  }

  @Test
  @DisplayName("Should return false when API key is blank")
  void shouldReturnFalseWhenApiKeyIsBlank() {
    provider.setTestConfig(createConfig("  ", true));

    assertFalse(provider.isAvailable());
  }

  @Test
  @DisplayName("Should return false when provider is not enabled")
  void shouldReturnFalseWhenNotEnabled() {
    provider.setTestConfig(createConfig("valid-api-key", false));

    assertFalse(provider.isAvailable());
  }

  @Test
  @DisplayName("Should return true when API key is present and provider is enabled")
  void shouldReturnTrueWhenApiKeyPresentAndEnabled() {
    provider.setTestConfig(createConfig("valid-api-key", true));

    assertTrue(provider.isAvailable());
  }

  @Test
  @DisplayName("Should throw IllegalStateException when provider is not available")
  void shouldThrowWhenProviderNotAvailable() {
    provider.setTestConfig(createConfig(null, false));

    assertThrows(IllegalStateException.class, () -> provider.extractReceiptData("test-image"));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  @DisplayName("Should extract receipt data successfully via REST call")
  void shouldExtractReceiptDataSuccessfully() throws Exception {
    var config = createConfig("valid-api-key", true);
    config.setModel("test-model");
    provider.setTestConfig(config);

    String responseJson =
        "{\"choices\": [{\"message\": {\"content\": \"{\\\"storeName\\\": \\\"TOKO MAJU\\\""
            + ",\\\"totalAmount\\\": 15000.0}\"}}]}";
    Map<String, Object> apiResponse = objectMapper.readValue(responseJson, Map.class);

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(ResponseEntity.ok(apiResponse));

    Map<String, Object> result = provider.extractReceiptData("test-image");

    assertNotNull(result);
    assertEquals("TOKO MAJU", result.get("storeName"));
    assertEquals(15000.0, result.get("totalAmount"));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  @DisplayName("Should throw RuntimeException when REST call returns non-2xx status")
  void shouldThrowOnNon2xxStatus() {
    var config = createConfig("valid-api-key", true);
    config.setModel("test-model");
    provider.setTestConfig(config);

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    assertThrows(RuntimeException.class, () -> provider.extractReceiptData("test-image"));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  @DisplayName("Should throw RuntimeException when REST call returns null body")
  void shouldThrowOnNullResponseBody() {
    var config = createConfig("valid-api-key", true);
    config.setModel("test-model");
    provider.setTestConfig(config);

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(ResponseEntity.ok().build());

    assertThrows(RuntimeException.class, () -> provider.extractReceiptData("test-image"));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  @DisplayName("Should return fallback map when response parsing fails")
  void shouldReturnFallbackOnParseFailure() {
    var config = createConfig("valid-api-key", true);
    config.setModel("test-model");
    provider.setTestConfig(config);

    var apiResponse = Map.<String, Object>of("choices", "invalid-format");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(ResponseEntity.ok(apiResponse));

    Map<String, Object> result = provider.extractReceiptData("test-image");

    assertNotNull(result);
    assertTrue(result.containsKey(LlmConstants.KEY_ERROR));
  }

  @Test
  @DisplayName("Should throw RuntimeException when REST call throws exception")
  void shouldThrowOnRestException() {
    var config = createConfig("valid-api-key", true);
    config.setModel("test-model");
    provider.setTestConfig(config);

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenThrow(new RuntimeException("Connection refused"));

    assertThrows(RuntimeException.class, () -> provider.extractReceiptData("test-image"));
  }

  private ProviderConfig createConfig(String apiKey, boolean enabled) {
    var config = new LlmProperties.ProviderConfig();
    config.setApiKey(apiKey);
    config.setEnabled(enabled);
    return config;
  }

  static class ConfigurableRestLlmProvider extends AbstractRestLlmProvider {

    private ProviderConfig testConfig = new LlmProperties.ProviderConfig();

    ConfigurableRestLlmProvider(
        LlmProperties llmProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
      super(llmProperties, restTemplate, objectMapper);
    }

    void setTestConfig(ProviderConfig config) {
      this.testConfig = config;
    }

    @Override
    protected String getApiUrl() {
      return "https://api.test-provider.com/v1/chat/completions";
    }

    @Override
    protected String getProviderNameConstant() {
      return "test-provider";
    }

    @Override
    protected ProviderConfig getConfig() {
      return testConfig;
    }
  }
}
