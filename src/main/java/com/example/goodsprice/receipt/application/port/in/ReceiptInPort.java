package com.example.goodsprice.receipt.application.port.in;

import com.example.goodsprice.receipt.application.domain.model.ReceiptCreateDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import java.util.List;
import java.util.UUID;

public interface ReceiptInPort {

  ReceiptDomain upload(byte[] imageBytes, String originalFilename);

  ReceiptDomain findById(UUID id);

  ReceiptStatus getStatus(UUID id);

  void approve(UUID id);

  void reject(UUID id);

  void process(UUID id, byte[] imageBytes);

  void insertItems(UUID id, List<java.util.Map<String, Object>> items);

  void create(ReceiptCreateDomain receiptCreateDomain);
}
