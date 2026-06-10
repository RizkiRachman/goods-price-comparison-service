package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.common.persistence.EntityMapperConfig;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(config = EntityMapperConfig.class)
public interface ProductMapper {

  ProductEntity toEntity(ProductDomain domain);

  ProductDomain toDomain(ProductEntity entity);
}
