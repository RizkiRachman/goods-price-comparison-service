package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class PriceMapper {

  public PriceEntity toEntity(PriceDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new PriceEntity();
    entity.setId(domain.getId());
    entity.setProductId(domain.getProductId());
    entity.setStoreId(domain.getStoreId());
    entity.setPrice(domain.getPrice());
    entity.setUnitPrice(domain.getUnitPrice());
    entity.setDateRecorded(domain.getDateRecorded());
    entity.setIsPromo(ObjectUtils.getOrDefault(domain, PriceDomain::getIsPromo, false));
    return entity;
  }

  public PriceDomain toDomain(PriceEntity entity) {
    if (Objects.isNull(entity)) return null;
    return PriceDomain.builder()
        .id(entity.getId())
        .productId(entity.getProductId())
        .storeId(entity.getStoreId())
        .price(entity.getPrice())
        .unitPrice(entity.getUnitPrice())
        .dateRecorded(entity.getDateRecorded())
        .isPromo(entity.getIsPromo())
        .build();
  }
}
