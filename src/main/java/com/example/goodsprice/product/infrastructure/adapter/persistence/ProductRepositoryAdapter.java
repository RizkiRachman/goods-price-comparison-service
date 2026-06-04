package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductRepositoryAdapter
    extends AbstractRepositoryAdapter<ProductDomain, Long, ProductEntity>
    implements ProductRepositoryPort {

  private final JpaProductRepository jpaRepo;
  private final ProductMapper mapper;

  public ProductRepositoryAdapter(JpaProductRepository jpaRepo, ProductMapper mapper) {
    this.jpaRepo = jpaRepo;
    this.mapper = mapper;
  }

  @Override
  protected JpaRepository<ProductEntity, Long> getJpaRepository() {
    return jpaRepo;
  }

  @Override
  protected ProductEntity toEntity(ProductDomain domain) {
    return mapper.toEntity(domain);
  }

  @Override
  protected ProductDomain toDomain(ProductEntity entity) {
    return mapper.toDomain(entity);
  }

  @Override
  public ProductDomain findByName(String name) {
    return jpaRepo.findByName(name).map(mapper::toDomain).orElse(null);
  }

  @Override
  public List<ProductDomain> searchByName(String name) {
    return jpaRepo.findByNameContainingIgnoreCase(name).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<ProductDomain> findAllByNames(List<String> names) {
    return jpaRepo.findByNameIn(names).stream().map(mapper::toDomain).toList();
  }

  @Override
  public boolean existsByName(String name) {
    return jpaRepo.existsByName(name);
  }

  @Override
  @Deprecated(forRemoval = true)
  public List<ProductDomain> findAll() {
    log.warn("Unbounded findAll() called - this may cause performance issues for large datasets");
    return jpaRepo.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public PageResponse<ProductDomain> search(ProductSearchCriteria criteria) {
    var spec = ProductSpecification.fromCriteria(criteria);
    var pr =
        new PageRequestDto(
            criteria.getPage(),
            criteria.getSize(),
            criteria.getSortBy(),
            criteria.getSortDirection());
    return PaginationHelper.findAll(pr, spec, jpaRepo, mapper::toDomain);
  }

  @Override
  public List<ProductDomain> findProductsNeedingSummaryUpdate(int limit) {
    return jpaRepo.findProductsNeedingSummaryUpdate(limit).stream().map(mapper::toDomain).toList();
  }

  @Override
  public void updateSummaryLastCalculated(Long productId, LocalDateTime timestamp) {
    jpaRepo.updateSummaryLastCalculated(productId, timestamp);
  }

  @Override
  public void updateSummaryLastCalculated(List<Long> productIds, LocalDateTime timestamp) {
    if (productIds.isEmpty()) return;
    jpaRepo.updateSummaryLastCalculated(productIds, timestamp);
  }

  @Override
  public void updateLastPriceUpdate(Long productId, LocalDateTime timestamp) {
    jpaRepo.updateLastPriceUpdate(productId, timestamp);
  }
}
