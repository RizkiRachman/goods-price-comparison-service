package com.example.goodsprice.llm.application.port.out;

import java.util.Map;

public interface LlmProviderPort {

  Map<String, Object> extractReceiptData(String imageBase64);

  String getProviderName();

  boolean isAvailable();
}
