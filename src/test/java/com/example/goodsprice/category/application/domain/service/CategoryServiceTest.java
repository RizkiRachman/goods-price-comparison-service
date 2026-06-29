package com.example.goodsprice.category.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.category.application.port.out.CategoryRepositoryPort;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.common.service.AbstractGenericServiceTest;
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
class CategoryServiceTest extends AbstractGenericServiceTest {

  @Mock private CategoryRepositoryPort categoryRepository;
  @InjectMocks private CategoryService categoryService;
  @Captor private ArgumentCaptor<CategoryDomain> categoryCaptor;

  private CategoryDomain existingCategory;

  @Override
  protected AbstractGenericService getService() {
    return categoryService;
  }

  @Override
  protected Object getExistingId() {
    return "FRUIT";
  }

  @Override
  protected Object getNonExistentId() {
    return "NONEXISTENT";
  }

  @Override
  protected Object getExistingEntity() {
    return existingCategory;
  }

  @Override
  protected String getNotFoundErrorCode() {
    return "CATEGORY_NOT_FOUND";
  }

  @Override
  protected GenericRepositoryPort getRepository() {
    return categoryRepository;
  }

  @BeforeEach
  void setUp() {
    existingCategory =
        CategoryDomain.builder()
            .id("FRUIT")
            .name("Fruits")
            .description("All kinds of fruits")
            .status("ACTIVE")
            .build();
  }

  @Test
  @DisplayName("Should create a new category")
  void shouldCreateCategory() {
    when(categoryRepository.save(any(CategoryDomain.class))).thenReturn(existingCategory);

    var domain =
        CategoryDomain.builder()
            .id("FRUIT")
            .name("Fruits")
            .description("All kinds of fruits")
            .build();
    var result = categoryService.create(domain);

    assertNotNull(result);
    assertEquals("FRUIT", result.getId());
    assertEquals("Fruits", result.getName());
    assertEquals("All kinds of fruits", result.getDescription());
    assertEquals("ACTIVE", result.getStatus());
    verify(categoryRepository).save(any(CategoryDomain.class));
  }

  @Test
  @DisplayName("Should update an existing category")
  void shouldUpdateExistingCategory() {
    when(categoryRepository.findById("FRUIT")).thenReturn(existingCategory);
    when(categoryRepository.save(any(CategoryDomain.class))).thenReturn(existingCategory);

    var domain =
        CategoryDomain.builder()
            .name("Fresh Fruits")
            .description("Fresh and organic fruits")
            .status("INACTIVE")
            .build();
    var result = categoryService.update("FRUIT", domain);

    assertEquals("Fresh Fruits", result.getName());
    assertEquals("Fresh and organic fruits", result.getDescription());
    assertEquals("INACTIVE", result.getStatus());
    verify(categoryRepository).save(categoryCaptor.capture());
    var saved = categoryCaptor.getValue();
    assertEquals("Fresh Fruits", saved.getName());
    assertEquals("Fresh and organic fruits", saved.getDescription());
    assertEquals("INACTIVE", saved.getStatus());
  }

  @Test
  @DisplayName("Should get all categories with pagination")
  void shouldReturnAllCategories() {
    var categories = List.of(existingCategory);
    var pageResponse = PageResponse.of(categories, 0, 10, categories.size());
    var criteria = new CategoryCriteria(null, null, null);
    when(categoryRepository.findAll(any(CategoryCriteria.class))).thenReturn(pageResponse);

    var result = categoryService.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.totalElements());
    assertEquals("Fruits", result.content().get(0).getName());
    verify(categoryRepository).findAll(any(CategoryCriteria.class));
  }
}
