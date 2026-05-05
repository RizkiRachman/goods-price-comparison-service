package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReceiptRepositoryAdapter implements ReceiptRepositoryPort {

  private final JpaReceiptRepository jpaRepo;
  private final ReceiptMapper mapper;

  @Override
  public ReceiptDomain save(ReceiptDomain receipt) {
    var entity = mapper.toEntity(receipt);
    var saved = jpaRepo.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public ReceiptDomain findById(UUID id) {
    var entity = jpaRepo.findById(id).orElse(null);
    if (Objects.isNull(entity)) return null;
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
  public boolean existsById(UUID id) {
    return jpaRepo.existsById(id);
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepo.deleteById(id);
  }
}
