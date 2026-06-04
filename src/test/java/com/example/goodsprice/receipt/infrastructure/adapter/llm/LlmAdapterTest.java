package com.example.goodsprice.receipt.infrastructure.adapter.llm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.goodsprice.llm.application.port.in.LlmInPort;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LlmAdapterTest {

  @Mock private LlmInPort llmService;

  @InjectMocks private LlmAdapter adapter;

  @Test
  void shouldExtractReceiptData() {
    var base64Image = Base64.getEncoder().encodeToString("test-image".getBytes());
    var expected = Map.<String, Object>of("storeName", "Toko Segar");
    when(llmService.extractReceipt(base64Image)).thenReturn(expected);

    var result = adapter.extractReceiptData(base64Image);

    assertNotNull(result);
    assertEquals("Toko Segar", result.get("storeName"));
  }

  @Test
  void shouldReturnProviderName() {
    when(llmService.getCurrentProvider()).thenReturn("gemini");

    var result = adapter.getProviderName();

    assertEquals("gemini", result);
  }

  @Test
  void shouldReturnIsAvailable() {
    when(llmService.isAvailable()).thenReturn(true);

    assertTrue(adapter.isAvailable());
  }

  @Test
  void shouldReturnIsNotAvailable() {
    when(llmService.isAvailable()).thenReturn(false);

    assertFalse(adapter.isAvailable());
  }

  @Test
  void shouldConvertToBase64() {
    var imageBytes = "test-image".getBytes();

    var result = adapter.toBase64(imageBytes);

    assertEquals(Base64.getEncoder().encodeToString(imageBytes), result);
  }
}
