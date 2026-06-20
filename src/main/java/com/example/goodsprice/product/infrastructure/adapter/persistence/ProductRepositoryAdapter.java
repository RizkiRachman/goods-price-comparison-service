package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.persistence.PaginationHelper;
import com.example.goodsprice.common.repository.AbstractRepositoryAdapter;
import com.example.goodsprice.config.CacheConfiguration;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ProductRepositoryAdapter
    extends AbstractRepositoryAdapter<ProductDomain, Long, ProductEntity>
    implements ProductRepositoryPort {

  private final JpaProductRepository jpaProductRepository;
  private final ProductMapper mapper;

  public ProductRepositoryAdapter(JpaProductRepository jpaRepository, ProductMapper mapper) {
    super(jpaRepository, mapper::toEntity, mapper::toDomain);
    this.jpaProductRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  @CachePut(value = CacheConfiguration.PRODUCTS_CACHE, key = "#result.id")
  public ProductDomain save(ProductDomain domain) {
    return super.save(domain);
  }

  @Override
  @Cacheable(CacheConfiguration.PRODUCTS_CACHE)
  public ProductDomain findById(Long id) {
    return super.findById(id);
  }

  @Override
  public ProductDomain findByName(String name) {
    return jpaProductRepository.findByName(name).map(mapper::toDomain).orElse(null);
  }

  @Override
  public List<ProductDomain> searchByName(String name) {
    return jpaProductRepository.findByNameContainingIgnoreCase(name).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<ProductDomain> findAllByNames(List<String> names) {
    return jpaProductRepository.findByNameIn(names).stream().map(mapper::toDomain).toList();
  }

  @Override
  public boolean existsByName(String name) {
    return jpaProductRepository.existsByName(name);
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
    return PaginationHelper.findAll(pr, spec, jpaSpecificationExecutor(), mapper::toDomain);
  }

  @Override
  public List<ProductDomain> findProductsNeedingSummaryUpdate(int limit) {
    return jpaProductRepository.findProductsNeedingSummaryUpdate(limit).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void updateSummaryLastCalculated(Long productId, LocalDateTime timestamp) {
    jpaProductRepository.updateSummaryLastCalculated(productId, timestamp);
  }

  @Override
  public void updateSummaryLastCalculated(List<Long> productIds, LocalDateTime timestamp) {
    if (productIds.isEmpty()) return;
    jpaProductRepository.updateSummaryLastCalculated(productIds, timestamp);
  }

  @Override
  public void updateLastPriceUpdate(Long productId, LocalDateTime timestamp) {
    jpaProductRepository.updateLastPriceUpdate(productId, timestamp);
  }
}
