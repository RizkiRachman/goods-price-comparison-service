package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.common.persistence.EntityMapperConfig;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceEntity;
import org.mapstruct.Mapper;

@Mapper(config = EntityMapperConfig.class)
public interface PriceMapper {

  PriceEntity toEntity(PriceDomain domain);

  PriceDomain toDomain(PriceEntity entity);
}
