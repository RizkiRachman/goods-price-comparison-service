package com.example.goodsprice.price.application.port.out;

import java.util.UUID;

@FunctionalInterface
public interface PriceSummaryEventOutPort {
  void publishPriceSummaryUpdateRequested(UUID receiptId);
}
