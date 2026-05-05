package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  public ProductEntity toEntity(ProductDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new ProductEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setCategory(domain.getCategory());
    entity.setBrand(domain.getBrand());
    entity.setUnit(domain.getUnit());
    entity.setStatus(domain.getStatus());
    entity.setLastPriceUpdate(domain.getLastPriceUpdate());
    entity.setSummaryLastCalculated(domain.getSummaryLastCalculated());
    return entity;
  }

  public ProductDomain toDomain(ProductEntity entity) {
    if (Objects.isNull(entity)) return null;
    return ProductDomain.builder()
        .id(entity.getId())
        .name(entity.getName())
        .category(entity.getCategory())
        .brand(entity.getBrand())
        .unit(entity.getUnit())
        .status(entity.getStatus())
        .lastPriceUpdate(entity.getLastPriceUpdate())
        .summaryLastCalculated(entity.getSummaryLastCalculated())
        .build();
  }
}
