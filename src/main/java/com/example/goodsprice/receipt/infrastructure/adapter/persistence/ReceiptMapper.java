package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import static com.example.goodsprice.common.util.ObjectUtils.getOrNull;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptStatusEntity;
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
    entity.setStatus(toStatusEntity(domain.getStatus()));
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
        .status(toDomainStatus(entity.getStatus()))
        .storeName(entity.getStoreName())
        .storeLocation(entity.getStoreLocation())
        .receiptDate(entity.getReceiptDate())
        .totalAmount(getOrNull(entity.getTotalAmount(), BigDecimal::valueOf))
        .extractedDataJson(entity.getExtractedDataJson())
        .errorMessage(entity.getErrorMessage())
        .imageData(entity.getImageData())
        .build();
  }

  private ReceiptStatusEntity toStatusEntity(ReceiptStatus status) {
    if (Objects.isNull(status)) return ReceiptStatusEntity.PENDING;
    return switch (status) {
      case PENDING -> ReceiptStatusEntity.PENDING;
      case PROCESSING -> ReceiptStatusEntity.PROCESSING;
      case PENDING_REVIEW -> ReceiptStatusEntity.PENDING_REVIEW;
      case APPROVED -> ReceiptStatusEntity.APPROVED;
      case REJECTED -> ReceiptStatusEntity.REJECTED;
      case COMPLETED -> ReceiptStatusEntity.COMPLETED;
      case FAILED -> ReceiptStatusEntity.FAILED;
    };
  }

  private ReceiptStatus toDomainStatus(ReceiptStatusEntity entityStatus) {
    if (Objects.isNull(entityStatus)) return ReceiptStatus.PENDING;
    return switch (entityStatus) {
      case PENDING -> ReceiptStatus.PENDING;
      case PROCESSING -> ReceiptStatus.PROCESSING;
      case PENDING_REVIEW -> ReceiptStatus.PENDING_REVIEW;
      case APPROVED -> ReceiptStatus.APPROVED;
      case REJECTED -> ReceiptStatus.REJECTED;
      case COMPLETED -> ReceiptStatus.COMPLETED;
      case FAILED -> ReceiptStatus.FAILED;
    };
  }
}
