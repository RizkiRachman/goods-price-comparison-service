package com.example.goodsprice.receipt.application.port.in;

import java.util.UUID;

public interface ReceiptApprovalInPort {
  void approve(UUID id);
}
