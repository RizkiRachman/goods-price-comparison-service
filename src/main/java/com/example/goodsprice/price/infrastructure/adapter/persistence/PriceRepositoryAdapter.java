package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.dto.PriceCriteria;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PriceRepositoryAdapter
    extends AbstractRepositoryAdapter<PriceDomain, Long, PriceEntity>
    implements PriceRepositoryPort {

  private final JpaPriceRepository jpaPriceRepository;
  private final PriceMapper mapper;

  public PriceRepositoryAdapter(JpaPriceRepository jpaRepository, PriceMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.jpaPriceRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public List<PriceDomain> saveAll(Iterable<PriceDomain> prices) {
    return super.saveAll(prices);
  }

  @Override
  public List<PriceDomain> findByProductId(Long productId) {
    return jpaPriceRepository.findByProductId(productId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<PriceDomain> findByProductIdAndDateRange(
      Long productId, LocalDate startDate, LocalDate endDate) {
    return jpaPriceRepository.findByProductIdAndDateRange(productId, startDate, endDate).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<PriceDomain> findCheapestByProductId(Long productId) {
    return jpaPriceRepository.findCheapestByProductId(productId).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<PriceDomain> findCheapestByProductIds(List<Long> productIds) {
    return jpaPriceRepository.findCheapestByProductIds(productIds).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<PriceDomain> findAllByProductIds(List<Long> productIds) {
    return jpaPriceRepository.findAllByProductIds(productIds).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Long> findProductIdsByStoreIds(List<Long> storeIds) {
    return jpaPriceRepository.findDistinctProductIdsByStoreIds(storeIds);
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
        jpaPriceRepository.findByProductIdWithFilters(
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
