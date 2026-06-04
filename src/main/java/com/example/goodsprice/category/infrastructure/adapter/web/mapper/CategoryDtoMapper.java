package com.example.goodsprice.category.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.common.util.ObjectUtils;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoMapper {

  public Category toApiCategory(CategoryDomain domain) {
    if (Objects.isNull(domain)) return null;
    var result = new Category();
    result.setId(domain.getId());
    result.setName(domain.getName());
    result.setDescription(
        ObjectUtils.getOrNull(
            domain.getDescription(), org.openapitools.jackson.nullable.JsonNullable::of));
    result.setStatus(ObjectUtils.getOrNull(domain.getStatus(), EntityStatus::fromValue));
    return result;
  }
}
