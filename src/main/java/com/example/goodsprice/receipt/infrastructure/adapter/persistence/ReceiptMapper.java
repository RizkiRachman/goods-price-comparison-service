package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import static com.example.goodsprice.common.util.ObjectUtils.getOrNull;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ReceiptMapper {

  public ReceiptEntity toEntity(ReceiptDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new ReceiptEntity();
    entity.setId(domain.getId());
    entity.setImageHash(domain.getImageHash());
    entity.setOriginalFilename(domain.getOriginalFilename());
    entity.setStatus(domain.getStatus());
    entity.setStoreName(domain.getStoreName());
    entity.setStoreLocation(domain.getStoreLocation());
    entity.setReceiptDate(domain.getReceiptDate());
    entity.setTotalAmount(getOrNull(domain.getTotalAmount(), BigDecimal::doubleValue));
    entity.setExtractedDataJson(domain.getExtractedDataJson());
    entity.setErrorMessage(domain.getErrorMessage());
    entity.setImageData(domain.getImageData());
    return entity;
  }

  public ReceiptDomain toDomain(ReceiptEntity entity) {
    if (Objects.isNull(entity)) return null;
    return ReceiptDomain.builder()
        .id(entity.getId())
        .imageHash(entity.getImageHash())
        .originalFilename(entity.getOriginalFilename())
        .status(entity.getStatus())
        .storeName(entity.getStoreName())
        .storeLocation(entity.getStoreLocation())
        .receiptDate(entity.getReceiptDate())
        .totalAmount(getOrNull(entity.getTotalAmount(), BigDecimal::valueOf))
        .extractedDataJson(entity.getExtractedDataJson())
        .errorMessage(entity.getErrorMessage())
        .imageData(entity.getImageData())
        .build();
  }
}
