package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.dto.PriceCriteria;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriceRepositoryAdapter
    extends AbstractRepositoryAdapter<PriceDomain, Long, PriceEntity>
    implements PriceRepositoryPort {

  private final JpaPriceRepository jpaRepo;
  private final PriceMapper mapper;

  public PriceRepositoryAdapter(JpaPriceRepository jpaRepo, PriceMapper mapper) {
    this.jpaRepo = jpaRepo;
    this.mapper = mapper;
  }

  @Override
  protected JpaRepository<PriceEntity, Long> getJpaRepository() {
    return jpaRepo;
  }

  @Override
  protected PriceEntity toEntity(PriceDomain domain) {
    return mapper.toEntity(domain);
  }

  @Override
  protected PriceDomain toDomain(PriceEntity entity) {
    return mapper.toDomain(entity);
  }

  @Override
  public List<PriceDomain> saveAll(Iterable<PriceDomain> prices) {
    var entities = new ArrayList<PriceEntity>();
    for (var price : prices) {
      entities.add(mapper.toEntity(price));
    }
    var saved = jpaRepo.saveAll(entities);
    return saved.stream().map(mapper::toDomain).toList();
  }

  @Override
  @Deprecated(forRemoval = true)
  public List<PriceDomain> findAll() {
    log.warn("Unbounded findAll() called - this may cause performance issues for large datasets");
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
  public List<Long> findProductIdsByStoreIds(List<Long> storeIds) {
    return jpaRepo.findDistinctProductIdsByStoreIds(storeIds);
  }

  @Override
  public PageResponse<PriceDomain> findByProductIdWithFilters(PriceCriteria criteria) {
    var sort =
        Sort.by(
            "desc".equalsIgnoreCase(criteria.pageRequest().sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            criteria.pageRequest().sortBy());
    var pageable =
        PageRequest.of(criteria.pageRequest().toZeroBased(), criteria.pageRequest().size(), sort);
    var page =
        jpaRepo.findByProductIdWithFilters(
            criteria.productId(),
            criteria.startDate(),
            criteria.endDate(),
            criteria.storeId(),
            criteria.isPromo(),
            pageable);
    var domains = page.getContent().stream().map(mapper::toDomain).toList();
    return PageResponse.of(
        domains,
        criteria.pageRequest().page(),
        criteria.pageRequest().size(),
        page.getTotalElements());
  }
}
