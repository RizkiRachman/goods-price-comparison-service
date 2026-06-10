package com.example.goodsprice.category.infrastructure.adapter.persistence;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import com.example.goodsprice.common.persistence.EntityMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = EntityMapperConfig.class)
public interface CategoryMapper {

  CategoryEntity toEntity(CategoryDomain domain);

  CategoryDomain toDomain(CategoryEntity entity);
}
