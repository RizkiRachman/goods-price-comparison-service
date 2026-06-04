package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ProductMapper {

  ProductEntity toEntity(ProductDomain domain);

  ProductDomain toDomain(ProductEntity entity);
}
