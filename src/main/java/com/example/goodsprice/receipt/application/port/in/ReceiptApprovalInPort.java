package com.example.goodsprice.receipt.application.port.in;

import java.util.UUID;

@FunctionalInterface
public interface ReceiptApprovalInPort {
  void approve(UUID id);
}
