package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceSummaryEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class PriceSummaryMapper {

  public PriceSummaryEntity toEntity(ProductPriceSummary domain) {
    if (Objects.isNull(domain)) {
      return null;
    }

    var entity = new PriceSummaryEntity();
    entity.setProductId(domain.getProductId());
    entity.setAvgPrice(domain.getAvgPrice());
    entity.setMinPrice(domain.getMinPrice());
    entity.setMaxPrice(domain.getMaxPrice());
    entity.setStoreCount(domain.getStoreCount());
    entity.setPriceCount(domain.getPriceCount());
    entity.setLastCalculatedAt(domain.getLastCalculatedAt());
    entity.setLastPriceDate(domain.getLastPriceDate());

    return entity;
  }

  public ProductPriceSummary toDomain(PriceSummaryEntity entity) {
    if (Objects.isNull(entity)) {
      return null;
    }

    return ProductPriceSummary.builder()
        .productId(entity.getProductId())
        .avgPrice(entity.getAvgPrice())
        .minPrice(entity.getMinPrice())
        .maxPrice(entity.getMaxPrice())
        .storeCount(entity.getStoreCount())
        .priceCount(entity.getPriceCount())
        .lastCalculatedAt(entity.getLastCalculatedAt())
        .lastPriceDate(entity.getLastPriceDate())
        .build();
  }
}
