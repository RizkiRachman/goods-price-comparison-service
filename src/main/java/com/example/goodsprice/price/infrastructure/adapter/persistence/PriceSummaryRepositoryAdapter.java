package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.application.port.out.PriceSummaryRepositoryPort;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceSummaryRepositoryAdapter implements PriceSummaryRepositoryPort {

  private final JpaPriceSummaryRepository jpaRepository;
  private final PriceSummaryMapper mapper;

  @Override
  public ProductPriceSummary save(ProductPriceSummary summary) {
    if (Objects.isNull(summary)) {
      return null;
    }
    var entity = mapper.toEntity(summary);
    var saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public List<ProductPriceSummary> saveAll(List<ProductPriceSummary> summaries) {
    if (Objects.isNull(summaries) || summaries.isEmpty()) {
      return List.of();
    }
    var entities = summaries.stream().map(mapper::toEntity).toList();
    var saved = jpaRepository.saveAll(entities);
    return saved.stream().map(mapper::toDomain).toList();
  }

  @Override
  public ProductPriceSummary findByProductId(Long productId) {
    if (Objects.isNull(productId)) {
      return null;
    }
    return jpaRepository.findById(productId).map(mapper::toDomain).orElse(null);
  }

  @Override
  public List<ProductPriceSummary> findByProductIds(Set<Long> productIds) {
    if (Objects.isNull(productIds) || productIds.isEmpty()) {
      return List.of();
    }
    return jpaRepository.findByProductIdIn(productIds).stream().map(mapper::toDomain).toList();
  }
}
