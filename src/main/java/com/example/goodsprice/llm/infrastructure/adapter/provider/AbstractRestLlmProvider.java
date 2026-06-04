package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.DATA_IMAGE_JPEG_PREFIX;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_CHOICES;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_CONTENT;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_IMAGE_URL;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_MAX_TOKENS;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_MESSAGE;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_MESSAGES;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_MODEL;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_RESPONSE_FORMAT;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_ROLE;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_TEMPERATURE;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_TEXT;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_TYPE;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_URL;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.GENERAL_VALUE_JSON_OBJECT;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.KEY_ERROR;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.KEY_RAW_TEXT;

import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import com.example.goodsprice.llm.infrastructure.config.LlmProperties.ProviderConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Abstract base class for REST-based LLM providers that share the same OpenAI-compatible API shape.
 *
 * <p>Subclasses only need to provide:
 *
 * <ul>
 *   <li>{@link #getApiUrl()} — the provider-specific API endpoint
 *   <li>{@link #getProviderNameConstant()} — the provider name constant (e.g. {@code "groq"})
 *   <li>{@link #getConfig()} — accessor for the provider's {@link ProviderConfig}
 * </ul>
 */
@Slf4j
public abstract class AbstractRestLlmProvider implements LlmProviderPort {

  protected final LlmProperties llmProperties;
  protected final RestTemplate restTemplate;
  protected final ObjectMapper objectMapper;

  protected AbstractRestLlmProvider(
      LlmProperties llmProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.llmProperties = llmProperties;
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  /** Provider-specific API URL (e.g., "https://api.groq.com/openai/v1/chat/completions"). */
  protected abstract String getApiUrl();

  /**
   * Provider name constant (e.g., {@link
   * com.example.goodsprice.llm.infrastructure.config.LlmConstants#PROVIDER_GROQ}).
   */
  protected abstract String getProviderNameConstant();

  /** Config accessor: {@code llmProperties.getGroq()}, {@code llmProperties.getSumopod()}. */
  protected abstract ProviderConfig getConfig();

  @Override
  public Map<String, Object> extractReceiptData(String imageBase64) {
    var config = getConfig();
    log.info("Extracting receipt data using {} model: {}", getProviderName(), config.getModel());

    if (!isAvailable()) {
      throw new IllegalStateException(
          "%s provider is not available. Check API key configuration."
              .formatted(getProviderName()));
    }

    try {
      var headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(config.getApiKey());

      var requestBody = buildRequestBody(imageBase64, config.getModel());
      var entity = new HttpEntity<>(requestBody, headers);

      ResponseEntity<Map> response = restTemplate.postForEntity(getApiUrl(), entity, Map.class);
      if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
        throw new RuntimeException(
            "%s API returned error: %s".formatted(getProviderName(), response.getStatusCode()));
      }

      return parseResponse(response.getBody());

    } catch (Exception e) {
      log.error(
          "Failed to extract receipt data from {} (model: {})",
          getProviderName(),
          config.getModel(),
          e);
      throw new RuntimeException(
          "Receipt extraction failed with %s model '%s': %s"
              .formatted(getProviderName(), config.getModel(), e.getMessage()),
          e);
    }
  }

  private Map<String, Object> buildRequestBody(String imageBase64, String model) {
    var prompt =
        """
You are an expert data extraction assistant for receipt images.

EXTRACTION RULES:
1. Store Name: UPPERCASE. Date: YYYY-MM-DD.
2. Items: List each line item. Ignore SUBTOTAL, TAX, TOTAL, DISCOUNT, barcode lines.
3. Product Name: UPPERCASE, clean whitespace.

CRITICAL — unitType Decision (read carefully):

Does the receipt show this item as SOLD BY WEIGHT (loose/bulk)?
→ Look for items like loose produce, bulk grains, where the receipt shows weight.
→ If YES → unitType = KILOGRAM. Quantity = weight as decimal (e.g., 0.07 for 70g).
→ unitPrice = price per KG. Math: quantity × unitPrice = totalPrice.
→ Example: "BAWANG PUTIH" (loose garlic, 0.07kg × Rp41,500 = Rp2,739).

Otherwise → unitType = PIECE. Quantity = count (always 1 if one unit).
→ unitPrice = totalPrice (same number, price per piece/package).
→ Math: quantity × unitPrice = totalPrice, so 1 × unitPrice = totalPrice.
→ Example: "SHRIMP ROLL 350GR" → PIECE, quantity=1, unitPrice=45000, totalPrice=45000.
→ Example: "BERAS ROJOLELE 5KG" → PIECE, quantity=1, unitPrice=75000, totalPrice=75000.
→ Example: "TELUR 10 BUTIR" → PIECE, quantity=10, unitPrice=2500, totalPrice=25000.
→ Example: "COCA COLA 1.5L" → PIECE, quantity=1, unitPrice=9000, totalPrice=9000.

REMEMBER: If product name has a weight like "350GR" or "5KG" or "500ML" — that's a
PACKAGE SIZE, not actual weight. Price is fixed per package. Use PIECE.

Standardized unitType list: [KILOGRAM, LITER, PIECE, DOZEN].
Categories: lowercase ("food", "drink", "produce", "dairy", "snack", "household", "unknown").

OUTPUT FORMAT (raw JSON, no markdown):
{
  "storeName": "string (UPPERCASE)",
  "date": "string (YYYY-MM-DD)",
  "items": [
    {
      "productName": "string (UPPERCASE)",
      "quantity": "number (>0)",
      "unitPrice": "number (positive float)",
      "totalPrice": "number (positive float)",
      "unitType": "string (KILOGRAM|LITER|PIECE|DOZEN)",
      "category": "string (lowercase)"
    }
  ],
  "totalAmount": "number (positive float)"
}
""";

    var imageUrl = DATA_IMAGE_JPEG_PREFIX + imageBase64;
    var messages =
        List.of(
            Map.of(
                GENERAL_ROLE,
                "user",
                GENERAL_CONTENT,
                List.of(
                    Map.of(GENERAL_TYPE, GENERAL_TEXT, GENERAL_TEXT, prompt),
                    Map.of(
                        GENERAL_TYPE,
                        GENERAL_IMAGE_URL,
                        GENERAL_IMAGE_URL,
                        Map.of(GENERAL_URL, imageUrl)))));

    var body = new HashMap<String, Object>();
    body.put(GENERAL_MODEL, model);
    body.put(GENERAL_MESSAGES, messages);
    body.put(GENERAL_MAX_TOKENS, 2000);
    body.put(GENERAL_TEMPERATURE, 0.1);
    body.put(GENERAL_RESPONSE_FORMAT, Map.of(GENERAL_TYPE, GENERAL_VALUE_JSON_OBJECT));
    return body;
  }

  private Map<String, Object> parseResponse(Map<String, Object> responseBody) {
    try {
      var choices = (List<Map<String, Object>>) responseBody.get(GENERAL_CHOICES);
      if (choices == null || choices.isEmpty()) {
        throw new RuntimeException("Empty response from %s API".formatted(getProviderName()));
      }

      var message = (Map<String, Object>) choices.get(0).get(GENERAL_MESSAGE);
      var content = (String) message.get(GENERAL_CONTENT);

      if (content == null || content.isBlank()) {
        throw new RuntimeException("Empty content in %s response".formatted(getProviderName()));
      }

      @SuppressWarnings("unchecked")
      var result = objectMapper.readValue(content, Map.class);
      log.debug("Successfully parsed structured response from {}", getProviderName());
      return result;

    } catch (Exception e) {
      log.error(
          "Failed to parse {} response: {}", getProviderName(), sanitizeForLog(responseBody), e);
      var fallback = new HashMap<String, Object>();
      fallback.put(KEY_RAW_TEXT, responseBody.toString());
      fallback.put(KEY_ERROR, "Failed to parse structured response: " + e.getMessage());
      return fallback;
    }
  }

  /**
   * Sanitizes an object's string representation for safe logging by removing control characters.
   *
   * @param value the value to sanitize
   * @return a sanitized string safe for log output
   */
  private String sanitizeForLog(Object value) {
    if (value == null) {
      return "null";
    }
    return value
        .toString()
        .replace('\r', ' ')
        .replace('\n', ' ')
        .replaceAll("[\\p{Cntrl}&&[^\t]]", "");
  }

  @Override
  public String getProviderName() {
    return getProviderNameConstant();
  }

  @Override
  public boolean isAvailable() {
    var config = getConfig();
    return config.getApiKey() != null && !config.getApiKey().isBlank() && config.isEnabled();
  }
}
