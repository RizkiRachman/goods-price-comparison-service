package com.example.goodsprice.receipt.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.ReceiptItem;
import com.example.goodsprice.api.model.Status;
import com.example.goodsprice.common.util.NumberUtils;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
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
}
