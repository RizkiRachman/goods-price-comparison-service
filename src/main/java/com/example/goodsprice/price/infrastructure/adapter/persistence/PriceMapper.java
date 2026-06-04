package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface PriceMapper {

  PriceEntity toEntity(PriceDomain domain);

  PriceDomain toDomain(PriceEntity entity);
}
