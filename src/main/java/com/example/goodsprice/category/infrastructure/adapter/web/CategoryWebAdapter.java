package com.example.goodsprice.category.infrastructure.adapter.web;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.api.model.CategoryListResponse;
import com.example.goodsprice.api.model.CreateCategoryRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.UpdateCategoryRequest;
import com.example.goodsprice.category.application.port.in.CategoryInPort;
import com.example.goodsprice.category.infrastructure.adapter.web.mapper.CategoryDtoMapper;
import com.example.goodsprice.common.util.ObjectUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryWebAdapter {

  private final CategoryInPort categoryInPort;
  private final CategoryDtoMapper mapper;

  public Category create(CreateCategoryRequest request) {
    var domain =
        categoryInPort.create(request.getId(), request.getName(), request.getDescription());
    return mapper.toApiCategory(domain);
  }

  public Category findById(String id) {
    return mapper.toApiCategory(categoryInPort.findById(id));
  }

  public CategoryListResponse list(
      Integer page,
      Integer pageSize,
      String search,
      EntityStatus status,
      String sortBy,
      String sortOrder) {
    var pageResponse =
        categoryInPort.findAll(
            ObjectUtils.getOrDefault(page, p -> p, 1),
            ObjectUtils.getOrDefault(pageSize, s -> s, 20),
            ObjectUtils.getOrDefault(sortBy, s -> s, "name"),
            ObjectUtils.getOrDefault(sortOrder, s -> s, "asc"),
            search,
            ObjectUtils.getOrNull(status, EntityStatus::getValue));

    var response = new CategoryListResponse();
    response.setData(pageResponse.content().stream().map(mapper::toApiCategory).toList());
    response.setPagination(pageResponse.toPagination());
    return response;
  }

  public Category update(String id, UpdateCategoryRequest request) {
    var domain =
        categoryInPort.update(
            id,
            request.getName(),
            resolveNullable(request.getDescription()),
            ObjectUtils.getOrNull(request.getStatus(), EntityStatus::getValue));
    return mapper.toApiCategory(domain);
  }

  private <T> T resolveNullable(JsonNullable<T> nullable) {
    if (Objects.isNull(nullable)) return null;
    return nullable.orElse(null);
  }
}
