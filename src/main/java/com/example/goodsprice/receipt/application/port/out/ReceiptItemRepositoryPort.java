package com.example.goodsprice.receipt.application.port.out;

import com.example.goodsprice.receipt.application.domain.model.ReceiptItemDomain;
import java.util.List;
import java.util.UUID;

public interface ReceiptItemRepositoryPort {

  void saveAll(List<ReceiptItemDomain> items);

  List<ReceiptItemDomain> findByReceiptId(UUID receiptId);
}
