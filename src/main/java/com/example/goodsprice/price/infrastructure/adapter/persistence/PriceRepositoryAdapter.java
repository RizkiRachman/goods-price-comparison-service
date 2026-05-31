package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Slf4j
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
  public List<PriceDomain> saveAll(Iterable<PriceDomain> prices) {
    var entities =
        new ArrayList<PriceEntity>();
    for (var price : prices) {
      entities.add(mapper.toEntity(price));
    }
    var saved = jpaRepo.saveAll(entities);
    return saved.stream().map(mapper::toDomain).toList();
  }

  @Override
  public PriceDomain findById(Long id) {
    return jpaRepo.findById(id).map(mapper::toDomain).orElse(null);
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
  public void deleteById(Long id) {
    jpaRepo.deleteById(id);
  }

  @Override
  public List<Long> findProductIdsByStoreIds(List<Long> storeIds) {
    return jpaRepo.findDistinctProductIdsByStoreIds(storeIds);
  }

  @Override
  public PageResponse<PriceDomain> findByProductIdWithFilters(
      Long productId,
      LocalDate startDate,
      LocalDate endDate,
      Long storeId,
      Boolean isPromo,
      PageRequestDto pageRequest) {
    var sort =
        Sort.by(
            "desc".equalsIgnoreCase(pageRequest.sortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            pageRequest.sortBy());
    var pageable = PageRequest.of(pageRequest.toZeroBased(), pageRequest.size(), sort);
    var page =
        jpaRepo.findByProductIdWithFilters(
            productId, startDate, endDate, storeId, isPromo, pageable);
    var domains = page.getContent().stream().map(mapper::toDomain).toList();
    return PageResponse.of(
        domains, pageRequest.page(), pageRequest.size(), page.getTotalElements());
  }
}
