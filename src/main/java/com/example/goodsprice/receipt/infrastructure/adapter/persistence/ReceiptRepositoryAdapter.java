package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ReceiptRepositoryAdapter
    extends AbstractRepositoryAdapter<ReceiptDomain, UUID, ReceiptEntity>
    implements ReceiptRepositoryPort {

  private final JpaReceiptRepository jpaReceiptRepository;
  private final ReceiptMapper mapper;

  public ReceiptRepositoryAdapter(JpaReceiptRepository jpaRepository, ReceiptMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.jpaReceiptRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public ReceiptDomain findByImageHash(String imageHash) {
    var entity = jpaReceiptRepository.findByImageHash(imageHash).orElse(null);
    if (Objects.isNull(entity)) return null;
    return mapper.toDomain(entity);
  }

  @Override
  public boolean existsByImageHash(String imageHash) {
    return jpaReceiptRepository.existsByImageHash(imageHash);
  }

  @Override
  public void updateImageData(UUID id, byte[] imageData) {
    jpaReceiptRepository.updateImageData(id, imageData);
  }
}
