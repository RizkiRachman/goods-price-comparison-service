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

  private static final String MOCK_STORE = "Mock Store";
  private static final String MOCK_DATE = "2024-01-01";
  private static final String MOCK_PRODUCT_MILK = "Milk";
  private static final String MOCK_PRODUCT_BREAD = "Bread";

  private final LlmProperties llmProperties;

  @Override
  public Map<String, Object> extractReceiptData(String imageBase64) {
    log.debug("Extracting receipt using local Ollama model");

    var result = new HashMap<String, Object>();
    result.put("store", MOCK_STORE);
    result.put("date", MOCK_DATE);
    result.put(
        "items",
        List.of(
            Map.of("name", MOCK_PRODUCT_MILK, "price", 5000, "quantity", 1),
            Map.of("name", MOCK_PRODUCT_BREAD, "price", 3000, "quantity", 2)));
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
