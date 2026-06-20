package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.application.port.out.PriceSummaryRepositoryPort;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceSummaryEntity;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PriceSummaryRepositoryAdapter
    extends AbstractRepositoryAdapter<ProductPriceSummary, Long, PriceSummaryEntity>
    implements PriceSummaryRepositoryPort {

  private final JpaPriceSummaryRepository jpaPriceSummaryRepository;
  private final PriceSummaryMapper mapper;

  public PriceSummaryRepositoryAdapter(
      JpaPriceSummaryRepository jpaRepository, PriceSummaryMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.jpaPriceSummaryRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public ProductPriceSummary save(ProductPriceSummary summary) {
    if (Objects.isNull(summary)) {
      return null;
    }
    var entity = mapper.toEntity(summary);
    var saved = jpaPriceSummaryRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public List<ProductPriceSummary> saveAll(List<ProductPriceSummary> summaries) {
    if (Objects.isNull(summaries) || summaries.isEmpty()) {
      return List.of();
    }
    var entities = summaries.stream().map(mapper::toEntity).toList();
    var saved = jpaPriceSummaryRepository.saveAll(entities);
    return saved.stream().map(mapper::toDomain).toList();
  }

  @Override
  public ProductPriceSummary findByProductId(Long productId) {
    if (Objects.isNull(productId)) {
      return null;
    }
    return jpaPriceSummaryRepository.findById(productId).map(mapper::toDomain).orElse(null);
  }

  @Override
  public List<ProductPriceSummary> findByProductIds(Set<Long> productIds) {
    if (Objects.isNull(productIds) || productIds.isEmpty()) {
      return List.of();
    }
    return jpaPriceSummaryRepository.findByProductIdIn(productIds).stream()
        .map(mapper::toDomain)
        .toList();
  }
}
