package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceRepositoryAdapter implements PriceRepositoryPort {

  private final JpaPriceRepository jpaRepo;
  private final PriceMapper mapper;

  @Override
  public PriceDomain save(PriceDomain price) {
    var entity = mapper.toEntity(price);
    var saved = jpaRepo.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public PriceDomain findById(Long id) {
    return jpaRepo.findById(id).map(mapper::toDomain).orElse(null);
  }

  @Override
  public List<PriceDomain> findAll() {
    return jpaRepo.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<PriceDomain> findByProductId(Long productId) {
    return jpaRepo.findByProductId(productId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<PriceDomain> findByProductIdAndDateRange(
      Long productId, LocalDate startDate, LocalDate endDate) {
    return jpaRepo.findByProductIdAndDateRange(productId, startDate, endDate).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<PriceDomain> findCheapestByProductId(Long productId) {
    return jpaRepo.findCheapestByProductId(productId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<PriceDomain> findCheapestByProductIds(List<Long> productIds) {
    return jpaRepo.findCheapestByProductIds(productIds).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<PriceDomain> findAllByProductIds(List<Long> productIds) {
    return jpaRepo.findAllByProductIds(productIds).stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaRepo.deleteById(id);
  }

  @Override
  public List<Long> findProductIdsByStoreIds(List<Long> storeIds) {
    return jpaRepo.findDistinctProductIdsByStoreIds(storeIds);
  }
}
