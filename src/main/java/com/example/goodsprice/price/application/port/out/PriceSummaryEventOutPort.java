package com.example.goodsprice.price.application.port.out;

import java.util.UUID;

public interface PriceSummaryEventOutPort {
  void publishPriceSummaryUpdateRequested(UUID receiptId);
}
