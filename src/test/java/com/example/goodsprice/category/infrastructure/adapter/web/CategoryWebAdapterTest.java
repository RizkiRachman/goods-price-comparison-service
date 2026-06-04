package com.example.goodsprice.category.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.api.model.CreateCategoryRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.UpdateCategoryRequest;
import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.CategoryInPort;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.category.infrastructure.adapter.web.mapper.CategoryDtoMapper;
import com.example.goodsprice.common.dto.PageResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryWebAdapterTest {

  @Mock private CategoryInPort categoryInPort;
  @Mock private CategoryDtoMapper mapper;

  @InjectMocks private CategoryWebAdapter categoryWebAdapter;

  @Captor private ArgumentCaptor<CategoryCriteria> criteriaCaptor;

  private CategoryDomain categoryDomain;
  private Category apiCategory;

  @BeforeEach
  void setUp() {
    categoryDomain =
        CategoryDomain.builder()
            .id("FRUIT")
            .name("Fruits")
            .description("All kinds of fruits")
            .status("ACTIVE")
            .build();

    apiCategory = new Category();
    apiCategory.setId("FRUIT");
    apiCategory.setName("Fruits");
  }

  @Test
  @DisplayName("Should create category from request")
  void shouldCreateCategory() {
    var request = new CreateCategoryRequest();
    request.setId("FRUIT");
    request.setName("Fruits");
    request.setDescription("All kinds of fruits");

    when(categoryInPort.create("FRUIT", "Fruits", "All kinds of fruits"))
        .thenReturn(categoryDomain);
    when(mapper.toApiCategory(categoryDomain)).thenReturn(apiCategory);

    var result = categoryWebAdapter.create(request);

    assertNotNull(result);
    assertEquals("FRUIT", result.getId());
    verify(categoryInPort).create("FRUIT", "Fruits", "All kinds of fruits");
  }

  @Test
  @DisplayName("Should find category by id")
  void shouldFindById() {
    when(categoryInPort.findById("FRUIT")).thenReturn(categoryDomain);
    when(mapper.toApiCategory(categoryDomain)).thenReturn(apiCategory);

    var result = categoryWebAdapter.findById("FRUIT");

    assertNotNull(result);
    assertEquals("FRUIT", result.getId());
  }

  @Test
  @DisplayName("Should list categories with pagination")
  void shouldListCategories() {
    var pageResponse = PageResponse.of(List.of(categoryDomain), 1, 20, 1);
    when(categoryInPort.findAll(any(CategoryCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiCategory(categoryDomain)).thenReturn(apiCategory);

    var result = categoryWebAdapter.list(1, 20, "fruit", EntityStatus.APPROVED, "name", "asc");

    assertNotNull(result);
    assertEquals(1, result.getData().size());
    assertEquals(1, result.getPagination().getTotalItems());
  }

  @Test
  @DisplayName("Should list categories with default pagination when null")
  void shouldListCategoriesWithDefaults() {
    var pageResponse = PageResponse.of(List.of(categoryDomain), 0, 20, 1);
    when(categoryInPort.findAll(any(CategoryCriteria.class))).thenReturn(pageResponse);
    when(mapper.toApiCategory(categoryDomain)).thenReturn(apiCategory);

    var result = categoryWebAdapter.list(null, null, null, null, null, null);

    assertNotNull(result);
    assertEquals(1, result.getData().size());
  }

  @Test
  @DisplayName("Should update category")
  void shouldUpdateCategory() {
    var request = new UpdateCategoryRequest();
    request.setName("Fresh Fruits");

    when(categoryInPort.update("FRUIT", "Fresh Fruits", null, null)).thenReturn(categoryDomain);
    when(mapper.toApiCategory(categoryDomain)).thenReturn(apiCategory);

    var result = categoryWebAdapter.update("FRUIT", request);

    assertNotNull(result);
    assertEquals("FRUIT", result.getId());
    verify(categoryInPort).update("FRUIT", "Fresh Fruits", null, null);
  }
}
