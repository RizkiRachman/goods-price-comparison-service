package com.example.goodsprice.category.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.web.mapper.DtoMapperSupport;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoMapper implements DtoMapperSupport {

  public Category toApiCategory(CategoryDomain domain) {
    return mapIfNotNull(
        domain,
        d -> {
          var result = new Category();
          result.setId(d.getId());
          result.setName(d.getName());
          result.setDescription(
              ObjectUtils.getOrNull(
                  d.getDescription(), org.openapitools.jackson.nullable.JsonNullable::of));
          result.setStatus(resolveStatusValue(d.getStatus()));
          return result;
        });
  }
}
