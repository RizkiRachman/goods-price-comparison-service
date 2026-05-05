package com.example.goodsprice.llm.application.port.in;

import java.util.Map;

public interface LlmInPort {

  Map<String, Object> extractReceipt(String imageBase64);

  String getCurrentProvider();

  boolean isAvailable();
}
