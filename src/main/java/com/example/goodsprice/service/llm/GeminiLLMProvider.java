package com.example.goodsprice.service.llm;

import com.example.goodsprice.config.properties.LlmProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Google Gemini LLM provider implementation.
 * Uses Gemini API for receipt OCR and text extraction.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiLLMProvider implements LLMProvider {

    private final LlmProperties llmProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> extractReceiptData(String imageBase64) {
        log.info("Extracting receipt data using Google Gemini");

        String apiKey = llmProperties.getGemini().getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key not configured. Set GEMINI_API_KEY environment variable.");
        }

        String model = llmProperties.getGemini().getModel();
        String url = String.format(
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
            model, apiKey
        );

        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, Object>> parts = List.of(
                Map.of("text", "Extract all items, prices, store name, and date from this receipt image. Return as JSON with fields: storeName, date, items (array with productName, quantity, unitPrice, totalPrice), totalAmount."),
                Map.of("inline_data", Map.of(
                    "mime_type", "image/jpeg",
                    "data", imageBase64
                ))
            );
            content.put("parts", parts);
            requestBody.put("contents", List.of(content));

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make request
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // Parse response
            return parseGeminiResponse(response.getBody());

        } catch (Exception e) {
            log.error("Failed to extract receipt data from Gemini", e);
            throw new RuntimeException("Receipt extraction failed: " + e.getMessage(), e);
        }
    }

    /**
     * Parse Gemini API response into structured data.
     */
    private Map<String, Object> parseGeminiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");

            if (candidates.isEmpty()) {
                throw new RuntimeException("No response from Gemini API");
            }

            String text = candidates.get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

            // Try to parse as JSON, otherwise return as text
            try {
                return objectMapper.readValue(text, Map.class);
            } catch (Exception e) {
                // Return raw text if not valid JSON
                Map<String, Object> result = new HashMap<>();
                result.put("rawText", text);
                return result;
            }

        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public boolean isAvailable() {
        String apiKey = llmProperties.getGemini().getApiKey();
        return apiKey != null && !apiKey.isEmpty();
    }
}
