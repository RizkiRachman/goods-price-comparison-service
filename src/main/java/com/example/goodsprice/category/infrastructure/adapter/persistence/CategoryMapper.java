package com.example.goodsprice.category.infrastructure.adapter.persistence;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface CategoryMapper {

  CategoryEntity toEntity(CategoryDomain domain);

  CategoryDomain toDomain(CategoryEntity entity);
}
