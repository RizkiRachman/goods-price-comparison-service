package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.common.persistence.EntityMapperConfig;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceSummaryEntity;
import org.mapstruct.Mapper;

@Mapper(config = EntityMapperConfig.class)
public interface PriceSummaryMapper {

  PriceSummaryEntity toEntity(ProductPriceSummary domain);

  ProductPriceSummary toDomain(PriceSummaryEntity entity);
}
