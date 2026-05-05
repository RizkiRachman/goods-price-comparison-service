package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.PROVIDER_LOCAL;

import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalLlmProvider implements LlmProviderPort {

  private final LlmProperties llmProperties;

  @Override
  public Map<String, Object> extractReceiptData(String imageBase64) {
    log.debug("Extracting receipt using local Ollama model");

    var result = new HashMap<String, Object>();
    result.put("store", "Mock Store");
    result.put("date", "2024-01-01");
    result.put(
        "items",
        List.of(
            Map.of("name", "Milk", "price", 5000, "quantity", 1),
            Map.of("name", "Bread", "price", 3000, "quantity", 2)));
    result.put("total", 11000);

    return result;
  }

  @Override
  public String getProviderName() {
    return PROVIDER_LOCAL;
  }

  @Override
  public boolean isAvailable() {
    if (!llmProperties.getLocal().isLocal()) {
      log.warn("Local provider is not configured as local type");
      return false;
    }
    return true;
  }
}
