package com.example.goodsprice.llm.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LlmServiceTest {

  @Mock private LlmProviderPort llmProvider;

  @InjectMocks private LlmService llmService;

  @Test
  @DisplayName("Should extract receipt successfully when LLM provider is available")
  void extractReceiptSuccess() {
    String imageBase64 = "test-image-base64";
    Map<String, Object> expectedReceiptData = Collections.singletonMap("key", "value");
    when(llmProvider.isAvailable()).thenReturn(true);
    when(llmProvider.extractReceiptData(imageBase64)).thenReturn(expectedReceiptData);

    Map<String, Object> actualReceiptData = llmService.extractReceipt(imageBase64);

    assertNotNull(actualReceiptData);
    assertEquals(expectedReceiptData, actualReceiptData);
    verify(llmProvider, times(1)).extractReceiptData(imageBase64);
  }

  @Test
  @DisplayName(
      "Should throw IllegalStateException when LLM provider is not available for receipt"
          + " extraction")
  void extractReceiptProviderNotAvailable() {
    String imageBase64 = "test-image-base64";
    when(llmProvider.isAvailable()).thenReturn(false);

    assertThrows(IllegalStateException.class, () -> llmService.extractReceipt(imageBase64));
    verify(llmProvider, never()).extractReceiptData(anyString());
  }

  @Test
  @DisplayName("Should generate consistent hash for the same image content")
  void generateImageHashConsistentHash() {
    String imageBase64 = "image-content-1";

    String hash1 = llmService.generateImageHash(imageBase64);
    String hash2 = llmService.generateImageHash(imageBase64);

    assertNotNull(hash1);
    assertEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should generate different hash for different image content")
  void generateImageHashDifferentHash() {
    String imageBase641 = "image-content-1";
    String imageBase642 = "image-content-2";

    String hash1 = llmService.generateImageHash(imageBase641);
    String hash2 = llmService.generateImageHash(imageBase642);

    assertNotNull(hash1);
    assertNotNull(hash2);
    assertNotEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should return the current LLM provider name")
  void getCurrentProviderReturnsProviderName() {
    String expectedProviderName = "MockLlmProvider";
    when(llmProvider.getProviderName()).thenReturn(expectedProviderName);

    String actualProviderName = llmService.getCurrentProvider();

    assertNotNull(actualProviderName);
    assertEquals(expectedProviderName, actualProviderName);
    verify(llmProvider, times(1)).getProviderName();
  }

  @Test
  @DisplayName("Should return true when LLM provider is available")
  void isAvailableTrue() {
    when(llmProvider.isAvailable()).thenReturn(true);

    boolean available = llmService.isAvailable();

    assertTrue(available);
    verify(llmProvider, times(1)).isAvailable();
  }

  @Test
  @DisplayName("Should return false when LLM provider is not available")
  void isAvailableFalse() {
    when(llmProvider.isAvailable()).thenReturn(false);

    boolean available = llmService.isAvailable();

    assertFalse(available);
    verify(llmProvider, times(1)).isAvailable();
  }

  @Test
  @DisplayName("Should extract receipt with realistic base64 image input")
  void extractReceiptWithRealBase64Input() {
    String realImageBase64 =
        "/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAAgEBAQFBQUFBQUFBQUFBQUFBQUFBQ"
            + "UJCQcFBQUKCQoMEBAKCQoJCRITCQkKCgoKCg0NDQ0NDQ0NDQ3/2wBDAQkJCQkJCQoJCQoN"
            + "CgoKDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0N";
    Map<String, Object> expectedReceiptData = Collections.singletonMap("storeName", "TOKO MAJU");
    when(llmProvider.isAvailable()).thenReturn(true);
    when(llmProvider.extractReceiptData(realImageBase64)).thenReturn(expectedReceiptData);

    Map<String, Object> actualReceiptData = llmService.extractReceipt(realImageBase64);

    assertNotNull(actualReceiptData);
    assertEquals(expectedReceiptData, actualReceiptData);
    assertEquals("TOKO MAJU", actualReceiptData.get("storeName"));
    verify(llmProvider, times(1)).extractReceiptData(realImageBase64);
  }

  @Test
  @DisplayName("Should return empty result when provider returns empty map for empty image")
  void extractReceiptWithEmptyImage() {
    String emptyImage = "";
    when(llmProvider.isAvailable()).thenReturn(true);
    when(llmProvider.extractReceiptData(emptyImage)).thenReturn(Collections.emptyMap());

    Map<String, Object> result = llmService.extractReceipt(emptyImage);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should generate 64-char hex hash for any input")
  void generateImageHashLength() {
    String hash = llmService.generateImageHash("any-image-data");

    assertNotNull(hash);
    assertEquals(64, hash.length());
  }

  @Test
  @DisplayName("Should generate hash for empty string input")
  void generateImageHashEmptyString() {
    String hash = llmService.generateImageHash("");

    assertNotNull(hash);
    assertFalse(hash.isEmpty());
  }
}
