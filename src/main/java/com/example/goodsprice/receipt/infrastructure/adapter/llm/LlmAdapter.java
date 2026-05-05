package com.example.goodsprice.receipt.infrastructure.adapter.llm;

import com.example.goodsprice.llm.application.port.in.LlmInPort;
import com.example.goodsprice.receipt.application.port.out.LlmProviderPort;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LlmAdapter implements LlmProviderPort {

  private final LlmInPort llmService;

  @Override
  public Map<String, Object> extractReceiptData(String imageBase64) {
    return llmService.extractReceipt(imageBase64);
  }

  public String toBase64(byte[] imageBytes) {
    return Base64.getEncoder().encodeToString(imageBytes);
  }
}
