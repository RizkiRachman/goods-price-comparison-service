package com.example.goodsprice.receipt.application.port.out;

import com.example.goodsprice.receipt.application.domain.model.ReceiptItem;
import java.util.List;
import java.util.UUID;

public interface ReceiptItemRepositoryPort {

  void saveAll(List<ReceiptItem> items);

  List<ReceiptItem> findByReceiptId(UUID receiptId);
}
