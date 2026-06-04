package com.example.goodsprice.receipt.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptItem;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.Status;
import com.example.goodsprice.common.util.DateUtils;
import com.example.goodsprice.common.util.JsonUtils;
import com.example.goodsprice.common.util.NumberUtils;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCreateDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptItemDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ReceiptDtoMapper {

  public Status toStatus(ReceiptStatus domainStatus) {
    if (Objects.isNull(domainStatus)) return Status.PENDING;
    return switch (domainStatus) {
      case PENDING, PROCESSING, PENDING_REVIEW -> Status.PENDING;
      case APPROVED -> Status.APPROVED;
      case REJECTED -> Status.REJECTED;
      case COMPLETED -> Status.COMPLETED;
      case FAILED -> Status.FAILED;
    };
  }

  public ReceiptItem toItem(Map<String, Object> itemData) {
    if (Objects.isNull(itemData)) return null;
    var item = new ReceiptItem();
    item.setProductName((String) itemData.get("productName"));
    item.setCategory((String) itemData.get("category"));
    item.setUnit((String) itemData.get("unitType"));
    item.setQuantity(NumberUtils.toDouble(itemData.get("quantity")));
    item.setUnitPrice(NumberUtils.toDouble(itemData.get("unitPrice")));
    item.setTotalPrice(NumberUtils.toDouble(itemData.get("totalPrice")));
    return item;
  }

  public ReceiptResultResponse toResultResponse(ReceiptDomain receipt) {
    if (Objects.isNull(receipt)) return null;
    var response = new ReceiptResultResponse();
    response.setReceiptId(receipt.getId());
    response.setStoreName(receipt.getStoreName());
    response.setStoreLocation(receipt.getStoreLocation());
    response.setTotalAmount(ObjectUtils.getOrNull(receipt.getTotalAmount(), d -> d.doubleValue()));
    response.setDate(parseDate(receipt.getReceiptDate()));
    var rawItems = JsonUtils.extractItems(receipt.getExtractedDataJson());
    response.setItems(rawItems.stream().map(this::toItem).filter(Objects::nonNull).toList());
    return response;
  }

  public ReceiptCreateDomain toCreateDomain(ReceiptCreateRequest request) {
    if (Objects.isNull(request)) return null;
    List<ReceiptItemDomain> items;
    if (Objects.isNull(request.getItems())) {
      items = Collections.emptyList();
    } else {
      items = request.getItems().stream().map(this::toItemDomain).toList();
    }
    return ReceiptCreateDomain.builder()
        .receiptDate(DateUtils.format(request.getDate(), DateUtils.ISO_DATE))
        .items(items)
        .storeName(request.getStoreName())
        .storeLocation(request.getStoreLocation())
        .totalAmount(ObjectUtils.getOrNull(request.getTotalAmount(), BigDecimal::valueOf))
        .build();
  }

  public ReceiptItemDomain toItemDomain(ReceiptItem item) {
    if (Objects.isNull(item)) return null;
    return ReceiptItemDomain.builder()
        .productName(item.getProductName())
        .unitPrice(item.getUnitPrice())
        .unit(item.getUnit())
        .totalPrice(item.getTotalPrice())
        .quantity(item.getQuantity())
        .category(item.getCategory())
        .build();
  }

  private LocalDate parseDate(String dateStr) {
    if (Objects.isNull(dateStr)) return null;
    try {
      return LocalDate.parse(dateStr);
    } catch (Exception e) {
      return null;
    }
  }
}
