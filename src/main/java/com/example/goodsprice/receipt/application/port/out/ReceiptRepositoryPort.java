package com.example.goodsprice.receipt.application.port.out;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import java.util.UUID;

public interface ReceiptRepositoryPort {

  ReceiptDomain save(ReceiptDomain receipt);

  ReceiptDomain findById(UUID id);

  ReceiptDomain findByImageHash(String imageHash);

  boolean existsByImageHash(String imageHash);

  boolean existsById(UUID id);

  void deleteById(UUID id);
}
