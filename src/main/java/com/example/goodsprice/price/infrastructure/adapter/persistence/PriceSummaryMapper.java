package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceSummaryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface PriceSummaryMapper {

  PriceSummaryEntity toEntity(ProductPriceSummary domain);

  ProductPriceSummary toDomain(PriceSummaryEntity entity);
}
