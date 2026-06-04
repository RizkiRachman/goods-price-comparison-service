package com.example.goodsprice.category.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.api.model.CategoryListResponse;
import com.example.goodsprice.api.model.CreateCategoryRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.UpdateCategoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  @Mock private CategoryWebAdapter adapter;

  @InjectMocks private CategoryController controller;

  private Category apiCategory;

  @BeforeEach
  void setUp() {
    apiCategory = new Category();
    apiCategory.setId("FRUIT");
    apiCategory.setName("Fruits");
  }

  @Test
  @DisplayName("Should create category via controller")
  void shouldCreateCategory() {
    var request = new CreateCategoryRequest();
    request.setId("FRUIT");
    request.setName("Fruits");

    when(adapter.create(request)).thenReturn(apiCategory);

    var response = controller.createCategory(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("FRUIT", response.getBody().getId());
    verify(adapter).create(request);
  }

  @Test
  @DisplayName("Should get category by id")
  void shouldGetCategory() {
    when(adapter.findById("FRUIT")).thenReturn(apiCategory);

    var response = controller.getCategory("FRUIT");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("FRUIT", response.getBody().getId());
    verify(adapter).findById("FRUIT");
  }

  @Test
  @DisplayName("Should list categories")
  void shouldListCategories() {
    var listResponse = new CategoryListResponse();

    when(adapter.list(1, 20, "search", EntityStatus.APPROVED, "name", "asc"))
        .thenReturn(listResponse);

    var response = controller.listCategories(1, 20, "search", EntityStatus.APPROVED, "name", "asc");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @DisplayName("Should update category")
  void shouldUpdateCategory() {
    var request = new UpdateCategoryRequest();
    request.setName("Fresh Fruits");

    when(adapter.update("FRUIT", request)).thenReturn(apiCategory);

    var response = controller.updateCategory("FRUIT", request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapter).update("FRUIT", request);
  }
}
