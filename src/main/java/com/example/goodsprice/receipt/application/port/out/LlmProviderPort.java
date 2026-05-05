package com.example.goodsprice.receipt.application.port.out;

import java.util.Map;

public interface LlmProviderPort {

  Map<String, Object> extractReceiptData(String imageBase64);
}
