package com.example.goodsprice.receipt.application.port.out;

import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import java.util.UUID;

public interface ReceiptRepositoryPort extends GenericRepositoryPort<ReceiptDomain, UUID> {

  ReceiptDomain findByImageHash(String imageHash);

  boolean existsByImageHash(String imageHash);

  void updateImageData(UUID id, byte[] imageData);
}
