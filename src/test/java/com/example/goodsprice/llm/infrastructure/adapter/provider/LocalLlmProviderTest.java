package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LocalLlmProviderTest {

  @Autowired private LocalLlmProvider localLlmProvider;

  @Test
  @DisplayName("Should have LocalLlmProvider bean configured")
  void shouldHaveLocalLlmProviderBean() {
    assertNotNull(localLlmProvider);
  }

  @Test
  @DisplayName("Should return provider name as local")
  void shouldReturnProviderName() {
    assertEquals("local", localLlmProvider.getProviderName());
  }

  @Test
  @DisplayName("Should be available in test profile")
  void shouldBeAvailable() {
    assertTrue(localLlmProvider.isAvailable());
  }

  @Test
  @DisplayName("Should extract receipt data with expected mock structure")
  void shouldExtractReceiptData() {
    String mockImage = "base64encodedimage";
    Map<String, Object> result = localLlmProvider.extractReceiptData(mockImage);

    assertNotNull(result);
    assertEquals("Mock Store", result.get("store"));
    assertEquals("2024-01-01", result.get("date"));
    assertEquals(11000, result.get("total"));
  }

  @Test
  @DisplayName("Should extract receipt items with correct mock values")
  void shouldExtractReceiptItems() {
    String mockImage = "base64encodedimage";
    Map<String, Object> result = localLlmProvider.extractReceiptData(mockImage);

    assertNotNull(result);
    assertTrue(result.containsKey("items"));

    var items = (List<Map<String, Object>>) result.get("items");
    assertNotNull(items);
    assertEquals(2, items.size());

    var firstItem = items.get(0);
    assertEquals("Milk", firstItem.get("name"));
    assertEquals(5000, firstItem.get("price"));
    assertEquals(1, firstItem.get("quantity"));

    var secondItem = items.get(1);
    assertEquals("Bread", secondItem.get("name"));
    assertEquals(3000, secondItem.get("price"));
    assertEquals(2, secondItem.get("quantity"));
  }
}
