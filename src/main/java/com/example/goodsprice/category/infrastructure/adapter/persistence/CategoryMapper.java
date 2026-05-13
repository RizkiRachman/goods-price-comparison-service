package com.example.goodsprice.category.infrastructure.adapter.persistence;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

  public CategoryEntity toEntity(CategoryDomain domain) {
    if (Objects.isNull(domain)) return null;
    var entity = new CategoryEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setDescription(domain.getDescription());
    entity.setStatus(domain.getStatus());
    return entity;
  }

  public CategoryDomain toDomain(CategoryEntity entity) {
    if (Objects.isNull(entity)) return null;
    return CategoryDomain.builder()
        .id(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .status(entity.getStatus())
        .build();
  }
}
