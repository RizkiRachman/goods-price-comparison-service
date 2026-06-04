package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReceiptRepositoryAdapter
    extends AbstractRepositoryAdapter<ReceiptDomain, UUID, ReceiptEntity>
    implements ReceiptRepositoryPort {

  private final JpaReceiptRepository jpaRepo;
  private final ReceiptMapper mapper;

  @Override
  protected JpaRepository<ReceiptEntity, UUID> getJpaRepository() {
    return jpaRepo;
  }

  @Override
  protected ReceiptEntity toEntity(ReceiptDomain domain) {
    return mapper.toEntity(domain);
  }

  @Override
  protected ReceiptDomain toDomain(ReceiptEntity entity) {
    return mapper.toDomain(entity);
  }

  @Override
  public ReceiptDomain findByImageHash(String imageHash) {
    var entity = jpaRepo.findByImageHash(imageHash).orElse(null);
    if (Objects.isNull(entity)) return null;
    return mapper.toDomain(entity);
  }

  @Override
  public boolean existsByImageHash(String imageHash) {
    return jpaRepo.existsByImageHash(imageHash);
  }

  @Override
  public void updateImageData(UUID id, byte[] imageData) {
    jpaRepo.updateImageData(id, imageData);
  }
}
