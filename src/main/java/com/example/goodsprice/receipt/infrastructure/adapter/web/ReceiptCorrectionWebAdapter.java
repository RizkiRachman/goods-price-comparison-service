package com.example.goodsprice.receipt.infrastructure.adapter.web;

import com.example.goodsprice.api.model.ReceiptCorrectRequest;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCorrectionDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptItemCorrectionDomain;
import com.example.goodsprice.receipt.application.port.in.ReceiptCorrectionInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.web.mapper.ReceiptDtoMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptCorrectionWebAdapter {

  private final ReceiptCorrectionInPort correctionInPort;
  private final ReceiptDtoMapper mapper;

  public ReceiptResultResponse correct(UUID receiptId, ReceiptCorrectRequest request) {
    var correction = toDomain(request);
    var receipt = correctionInPort.correct(receiptId, correction);
    log.info("Correction submitted for receipt: {}", receiptId);
    return mapper.toResultResponse(receipt);
  }

  private ReceiptCorrectionDomain toDomain(ReceiptCorrectRequest request) {
    var totalAmount =
        Objects.nonNull(request.getTotalAmount())
            ? BigDecimal.valueOf(request.getTotalAmount())
            : null;

    var items =
        Objects.nonNull(request.getItems())
            ? request.getItems().stream().map(this::toItemDomain).toList()
            : List.<ReceiptItemCorrectionDomain>of();

    return ReceiptCorrectionDomain.builder()
        .storeName(request.getStoreName())
        .storeLocation(request.getStoreLocation())
        .receiptDate(Objects.nonNull(request.getDate()) ? request.getDate().toString() : null)
        .totalAmount(totalAmount)
        .items(items)
        .build();
  }

  private ReceiptItemCorrectionDomain toItemDomain(
      com.example.goodsprice.api.model.ReceiptItem item) {
    return ReceiptItemCorrectionDomain.builder()
        .productName(item.getProductName())
        .category(item.getCategory())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .totalPrice(item.getTotalPrice())
        .unit(item.getUnit())
        .build();
  }
}
