package com.example.goodsprice.category.infrastructure.adapter.web;

import static com.example.goodsprice.common.util.JsonNullableUtils.resolveNullable;
import static com.example.goodsprice.common.util.PaginationUtils.resolvePage;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSize;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSortBy;
import static com.example.goodsprice.common.util.PaginationUtils.resolveSortOrder;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.api.model.CategoryListResponse;
import com.example.goodsprice.api.model.CreateCategoryRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.UpdateCategoryRequest;
import com.example.goodsprice.category.application.port.in.CategoryInPort;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.category.infrastructure.adapter.web.mapper.CategoryDtoMapper;
import com.example.goodsprice.common.constant.AppConstants;
import com.example.goodsprice.common.dto.PageRequestDto;
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
    var pageRequest =
        new PageRequestDto(
            resolvePage(page, 1),
            resolveSize(pageSize, AppConstants.DEFAULT_PAGE_SIZE),
            resolveSortBy(sortBy, "name"),
            resolveSortOrder(sortOrder, "asc"));
    var criteria =
        new CategoryCriteria(
            pageRequest, search, ObjectUtils.getOrNull(status, EntityStatus::getValue));
    var pageResponse = categoryInPort.findAll(criteria);

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
}
