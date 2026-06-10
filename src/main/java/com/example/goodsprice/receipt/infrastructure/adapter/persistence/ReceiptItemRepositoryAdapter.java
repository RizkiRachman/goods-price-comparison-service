package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import com.example.goodsprice.receipt.application.domain.model.ReceiptItemDomain;
import com.example.goodsprice.receipt.application.port.out.ReceiptItemRepositoryPort;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptItemEntity;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReceiptItemRepositoryAdapter implements ReceiptItemRepositoryPort {

  private final JpaReceiptItemRepository jpaRepo;

  @Override
  public void saveAll(List<ReceiptItemDomain> items) {
    var entities = items.stream().map(this::toEntity).toList();
    jpaRepo.saveAll(entities);
  }

  @Override
  public List<ReceiptItemDomain> findByReceiptId(UUID receiptId) {
    return jpaRepo.findByReceiptId(receiptId).stream().map(this::toDomain).toList();
  }

  private ReceiptItemEntity toEntity(ReceiptItemDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new ReceiptItemEntity();
    entity.setReceiptId(domain.getReceiptId());
    entity.setProductName(domain.getProductName());
    entity.setCategory(domain.getCategory());
    entity.setQuantity(domain.getQuantity());
    entity.setUnitPrice(domain.getUnitPrice());
    entity.setTotalPrice(domain.getTotalPrice());
    entity.setUnit(domain.getUnit());
    return entity;
  }

  private ReceiptItemDomain toDomain(ReceiptItemEntity entity) {
    if (Objects.isNull(entity)) return null;
    return ReceiptItemDomain.builder()
        .id(entity.getId())
        .receiptId(entity.getReceiptId())
        .productName(entity.getProductName())
        .category(entity.getCategory())
        .quantity(entity.getQuantity())
        .unitPrice(entity.getUnitPrice())
        .totalPrice(entity.getTotalPrice())
        .unit(entity.getUnit())
        .build();
  }
}
