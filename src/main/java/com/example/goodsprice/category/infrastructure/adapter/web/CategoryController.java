package com.example.goodsprice.category.infrastructure.adapter.web;

import static com.example.goodsprice.common.web.ControllerResponse.created;
import static com.example.goodsprice.common.web.ControllerResponse.ok;

import com.example.goodsprice.api.controller.CategoriesApi;
import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.api.model.CategoryListResponse;
import com.example.goodsprice.api.model.CreateCategoryRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.UpdateCategoryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoriesApi {

  private final CategoryWebAdapter adapter;

  @Override
  public ResponseEntity<Category> createCategory(@Valid CreateCategoryRequest request) {
    var category = adapter.create(request);
    return created(category);
  }

  @Override
  public ResponseEntity<Category> getCategory(String categoryId) {
    return ok(adapter.findById(categoryId));
  }

  @Override
  public ResponseEntity<CategoryListResponse> listCategories(
      Integer page,
      Integer pageSize,
      String search,
      EntityStatus status,
      String sortBy,
      String sortOrder) {
    return ok(adapter.list(page, pageSize, search, status, sortBy, sortOrder));
  }

  @Override
  public ResponseEntity<Category> updateCategory(
      String categoryId, @Valid UpdateCategoryRequest request) {
    return ok(adapter.update(categoryId, request));
  }
}
