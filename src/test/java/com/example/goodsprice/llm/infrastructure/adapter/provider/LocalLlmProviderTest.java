package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalLlmProviderTest {

  @Mock private LlmProperties llmProperties;

  private LocalLlmProvider provider;

  @BeforeEach
  void setUp() {
    provider = new LocalLlmProvider(llmProperties);
  }

  @Test
  @DisplayName("Should return correct provider name")
  void shouldReturnProviderName() {
    assertEquals("local", provider.getProviderName());
  }

  @Test
  @DisplayName("Should return true when local provider type is configured as local")
  void shouldBeAvailableWhenLocalType() {
    var localConfig = new LlmProperties.ProviderConfig();
    localConfig.setType("local");
    doReturn(localConfig).when(llmProperties).getLocal();

    assertTrue(provider.isAvailable());
  }

  @Test
  @DisplayName("Should return false when local provider type is not local")
  void shouldNotBeAvailableWhenNotLocalType() {
    var localConfig = new LlmProperties.ProviderConfig();
    localConfig.setType("cloud");
    doReturn(localConfig).when(llmProperties).getLocal();

    assertFalse(provider.isAvailable());
  }

  @Test
  @DisplayName("Should extract receipt data and return mock values")
  void shouldExtractReceiptData() {
    Map<String, Object> result = provider.extractReceiptData("any-image");

    assertNotNull(result);
    assertEquals("Mock Store", result.get("store"));
    assertEquals("2024-01-01", result.get("date"));
    assertEquals(11000, result.get("total"));

    Object itemsObj = result.get("items");
    assertNotNull(itemsObj);
    assertTrue(itemsObj instanceof List);

    List<?> items = (List<?>) itemsObj;
    assertEquals(2, items.size());

    Map<?, ?> firstItem = (Map<?, ?>) items.get(0);
    assertEquals("Milk", firstItem.get("name"));
    assertEquals(5000, firstItem.get("price"));

    Map<?, ?> secondItem = (Map<?, ?>) items.get(1);
    assertEquals("Bread", secondItem.get("name"));
    assertEquals(3000, secondItem.get("price"));
  }

  @Test
  @DisplayName("Should return mock data regardless of image input")
  void shouldReturnMockDataForAnyInput() {
    Map<String, Object> resultEmpty = provider.extractReceiptData("");
    Map<String, Object> resultOther = provider.extractReceiptData("different-image");

    assertNotNull(resultEmpty);
    assertNotNull(resultOther);
    assertEquals(resultEmpty.get("store"), resultOther.get("store"));
  }

  @Test
  @DisplayName("Should return mock items with correct structure")
  void shouldReturnMockItemsWithCorrectStructure() {
    Map<String, Object> result = provider.extractReceiptData("test-image");

    List<?> items = (List<?>) result.get("items");
    Map<?, ?> milk = (Map<?, ?>) items.get(0);
    assertEquals("Milk", milk.get("name"));
    assertEquals(5000, milk.get("price"));
    assertEquals(1, milk.get("quantity"));

    Map<?, ?> bread = (Map<?, ?>) items.get(1);
    assertEquals("Bread", bread.get("name"));
    assertEquals(3000, bread.get("price"));
    assertEquals(2, bread.get("quantity"));
  }
}
