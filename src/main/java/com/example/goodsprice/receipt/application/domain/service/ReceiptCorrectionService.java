package com.example.goodsprice.receipt.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCorrectionDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptItemCorrectionDomain;
import com.example.goodsprice.receipt.application.port.in.ReceiptCorrectionInPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptCorrectionService implements ReceiptCorrectionInPort {

  private final ReceiptRepositoryPort receiptRepository;
  private final ReceiptEventOutPort eventOutPort;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  @ActivityLog
  public ReceiptDomain correct(UUID receiptId, ReceiptCorrectionDomain correction) {
    var receipt = receiptRepository.findById(receiptId);

    if (Objects.isNull(receipt)) {
      throw NotFoundException.receipt(receiptId);
    }

    if (Objects.nonNull(correction.getStoreName())) receipt.setStoreName(correction.getStoreName());
    if (Objects.nonNull(correction.getStoreLocation()))
      receipt.setStoreLocation(correction.getStoreLocation());
    if (Objects.nonNull(correction.getReceiptDate()))
      receipt.setReceiptDate(correction.getReceiptDate());
    if (Objects.nonNull(correction.getTotalAmount()))
      receipt.setTotalAmount(correction.getTotalAmount());

    var items = correction.getItems();
    if (Objects.nonNull(items) && !items.isEmpty()) {
      receipt.setExtractedDataJson(toItemsJson(items));
    }

    var saved = receiptRepository.save(receipt);
    eventOutPort.publishReceiptCorrected(saved);
    log.info("Receipt correction applied: {}", receiptId);
    return saved;
  }

  private String toItemsJson(List<ReceiptItemCorrectionDomain> items) {
    var itemsData =
        items.stream()
            .map(
                item ->
                    Map.<String, Object>of(
                        "productName", item.getProductName(),
                        "category", item.getCategory(),
                        "quantity", item.getQuantity(),
                        "unitPrice", item.getUnitPrice(),
                        "totalPrice", item.getTotalPrice(),
                        "unitType", item.getUnit()))
            .toList();
    try {
      return objectMapper.writeValueAsString(Map.of("items", itemsData));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize correction items", e);
    }
  }
}
