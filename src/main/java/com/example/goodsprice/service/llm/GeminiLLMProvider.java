package com.example.goodsprice.service.llm;

import com.example.goodsprice.config.properties.LlmProperties;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Google Gemini LLM provider implementation using official Google GenAI SDK.
 * Uses Gemini API for receipt OCR and text extraction.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiLLMProvider implements LLMProvider {

    private final LlmProperties llmProperties;

    @Override
    public Map<String, Object> extractReceiptData(String imageBase64) {
        log.info("Extracting receipt data using Google Gemini SDK");

        String apiKey = llmProperties.getGemini().getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key not configured. Set GEMINI_API_KEY environment variable.");
        }

        String model = llmProperties.getGemini().getModel();

        try {
            // Create client with API key
            Client client = Client.builder().apiKey(apiKey).build();

            // Build the prompt
            String prompt = """
                Extract all items, prices, store name, and date from this receipt image.
                Additional verification:
                - ensure prices are correctly identified as positive values. If any price is negative, correct it to a positive value in the output.
                - ensure quantities are correctly identified as positive integers. If any quantity is negative or zero, correct it to a positive integer in the output.
                - ensure location is able to extract
                - ensure date is able to extract
                Return as JSON with fields:
                - storeName: string
                - date: string (ISO format)
                - items: array of objects with productName, quantity, unitPrice, totalPrice
                - totalAmount: number
                Only return the JSON, no markdown formatting.
                """;

            // Build content with text and image
            Content content = Content.builder()
                .role("user")
                .parts(java.util.List.of(
                    Part.builder().text(prompt).build(),
                    Part.builder().inlineData(
                        com.google.genai.types.Blob.builder()
                            .mimeType("image/jpeg")
                            .data(Base64.getDecoder().decode(imageBase64))
                            .build()
                    ).build()
                ))
                .build();

            // Generate content
            GenerateContentResponse response = client.models.generateContent(
                model,
                content,
                GenerateContentConfig.builder().build()
            );

            // Parse the response
            String text = response.text();
            return parseResponse(text);

        } catch (Exception e) {
            log.error("Failed to extract receipt data from Gemini", e);
            throw new RuntimeException("Receipt extraction failed: " + e.getMessage(), e);
        }
    }

    /**
     * Parse Gemini response text into structured data.
     */
    private Map<String, Object> parseResponse(String text) {
        Map<String, Object> result = new HashMap<>();

        if (text == null || text.isEmpty()) {
            result.put("error", "Empty response from Gemini");
            return result;
        }

        // Try to extract JSON from markdown code blocks
        String jsonText = text;
        if (text.contains("```json")) {
            jsonText = text.substring(text.indexOf("```json") + 7, text.lastIndexOf("```")).trim();
        } else if (text.contains("```")) {
            jsonText = text.substring(text.indexOf("```") + 3, text.lastIndexOf("```")).trim();
        }

        try {
            // Try to parse as JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(jsonText, Map.class);
        } catch (Exception e) {
            log.warn("Could not parse response as JSON, returning raw text");
            result.put("rawText", text);
            return result;
        }
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public boolean isAvailable() {
        // Check if provider type is configured as cloud (not local)
        if (llmProperties.getGemini().isLocal()) {
            log.warn("Gemini provider is configured as local type, but it's a cloud service");
            return false;
        }

        String apiKey = llmProperties.getGemini().getApiKey();
        return apiKey != null && !apiKey.isEmpty();
    }
}
