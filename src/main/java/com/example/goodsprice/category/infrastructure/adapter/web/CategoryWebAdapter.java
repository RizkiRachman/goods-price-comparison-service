package com.example.goodsprice.category.infrastructure.adapter.web;

import static com.example.goodsprice.common.util.JsonNullableUtils.resolveNullable;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.api.model.CategoryListResponse;
import com.example.goodsprice.api.model.CreateCategoryRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.UpdateCategoryRequest;
import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.CategoryInPort;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.category.infrastructure.adapter.web.mapper.CategoryDtoMapper;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.web.AbstractCrudWebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryWebAdapter extends AbstractCrudWebAdapter {

  private final CategoryInPort categoryInPort;
  private final CategoryDtoMapper mapper;

  public Category create(CreateCategoryRequest request) {
    var domain =
        CategoryDomain.builder()
            .id(request.getId())
            .name(request.getName())
            .description(request.getDescription())
            .build();
    return mapper.toApiCategory(categoryInPort.create(domain));
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
    var params = resolvePagination(page, pageSize, sortBy, sortOrder, "name", "asc");
    var pageRequest = buildPageRequest(params);
    var criteria =
        new CategoryCriteria(
            pageRequest, search, ObjectUtils.getOrNull(status, EntityStatus::getValue));
    var pageResponse = categoryInPort.findAll(criteria);

    return buildTypedListResponse(pageResponse, mapper::toApiCategory, CategoryListResponse::new);
  }

  public Category update(String id, UpdateCategoryRequest request) {
    var domain =
        CategoryDomain.builder()
            .name(request.getName())
            .description(resolveNullable(request.getDescription()))
            .status(ObjectUtils.getOrNull(request.getStatus(), EntityStatus::getValue))
            .build();
    return mapper.toApiCategory(categoryInPort.update(id, domain));
  }
}
