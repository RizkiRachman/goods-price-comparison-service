package com.example.goodsprice.receipt.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.ReceiptItem;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.Status;
import com.example.goodsprice.common.util.JsonUtils;
import com.example.goodsprice.common.util.NumberUtils;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import java.time.LocalDate;
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

  private LocalDate parseDate(String dateStr) {
    if (Objects.isNull(dateStr)) return null;
    try {
      return LocalDate.parse(dateStr);
    } catch (Exception e) {
      return null;
    }
  }
}
