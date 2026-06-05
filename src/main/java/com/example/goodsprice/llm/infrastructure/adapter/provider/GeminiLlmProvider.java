package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.CODE_BLOCK_MARKER;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.JSON_CODE_BLOCK_MARKER;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.KEY_ERROR;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.KEY_RAW_TEXT;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.MIME_IMAGE_JPEG;
import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.PROVIDER_GEMINI;

import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Blob;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiLlmProvider implements LlmProviderPort {

  private final LlmProperties llmProperties;

  @Override
  public Map<String, Object> extractReceiptData(String imageBase64) {
    log.info("Extracting receipt data using Google Gemini SDK");

    var apiKey = llmProperties.getGemini().getApiKey();
    if (Objects.isNull(apiKey) || apiKey.isEmpty()) {
      throw new IllegalStateException(
          "Gemini API key not configured. Set GEMINI_API_KEY environment variable.");
    }

    var model = llmProperties.getGemini().getModel();

    try {
      var client = Client.builder().apiKey(apiKey).build();

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

      var content =
          Content.builder()
              .role("user")
              .parts(
                  List.of(
                      Part.builder().text(prompt).build(),
                      Part.builder()
                          .inlineData(
                              Blob.builder()
                                  .mimeType(MIME_IMAGE_JPEG)
                                  .data(Base64.getDecoder().decode(imageBase64))
                                  .build())
                          .build()))
              .build();

      var response =
          client.models.generateContent(model, content, GenerateContentConfig.builder().build());
      var text = response.text();
      return parseResponse(text);

    } catch (Exception e) {
      log.error("Failed to extract receipt data from Gemini", e);
      throw new RuntimeException("Receipt extraction failed: " + e.getMessage(), e);
    }
  }

  private Map<String, Object> parseResponse(String text) {
    var result = new HashMap<String, Object>();
    if (Objects.isNull(text) || text.isEmpty()) {
      result.put(KEY_ERROR, "Empty response from Gemini");
      return result;
    }

    var jsonText = text;
    if (text.contains(JSON_CODE_BLOCK_MARKER)) {
      jsonText =
          text.substring(
                  text.indexOf(JSON_CODE_BLOCK_MARKER) + JSON_CODE_BLOCK_MARKER.length(),
                  text.lastIndexOf(CODE_BLOCK_MARKER))
              .trim();
    } else if (text.contains(CODE_BLOCK_MARKER)) {
      jsonText =
          text.substring(
                  text.indexOf(CODE_BLOCK_MARKER) + CODE_BLOCK_MARKER.length(),
                  text.lastIndexOf(CODE_BLOCK_MARKER))
              .trim();
    }

    try {
      var mapper = new ObjectMapper();
      return mapper.readValue(jsonText, Map.class);
    } catch (Exception e) {
      log.warn("Could not parse response as JSON, returning raw text");
      result.put(KEY_RAW_TEXT, text);
      return result;
    }
  }

  @Override
  public String getProviderName() {
    return PROVIDER_GEMINI;
  }

  @Override
  public boolean isAvailable() {
    if (llmProperties.getGemini().isLocal()) {
      log.warn("Gemini provider is configured as local type, but it's a cloud service");
      return false;
    }
    var apiKey = llmProperties.getGemini().getApiKey();
    return Objects.nonNull(apiKey) && !apiKey.isEmpty();
  }
}
