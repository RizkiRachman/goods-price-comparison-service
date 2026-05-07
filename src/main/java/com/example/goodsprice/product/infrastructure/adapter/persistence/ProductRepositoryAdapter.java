package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

  private final JpaProductRepository jpaRepo;
  private final ProductMapper mapper;

  @Override
  public ProductDomain save(ProductDomain product) {
    var entity = mapper.toEntity(product);
    var saved = jpaRepo.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public ProductDomain findById(Long id) {
    return jpaRepo.findById(id).map(mapper::toDomain).orElse(null);
  }

  @Override
  public ProductDomain findByName(String name) {
    return jpaRepo.findByName(name).map(mapper::toDomain).orElse(null);
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
  public List<ProductDomain> findAll() {
    return jpaRepo.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaRepo.deleteById(id);
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
  public void updateLastPriceUpdate(Long productId, LocalDateTime timestamp) {
    jpaRepo.updateLastPriceUpdate(productId, timestamp);
  }
}
