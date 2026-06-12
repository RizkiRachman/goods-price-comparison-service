package com.example.goodsprice.category.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.application.port.in.dto.CategoryCriteria;
import com.example.goodsprice.category.application.port.out.CategoryRepositoryPort;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
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
class CategoryServiceTest {

  @Mock private CategoryRepositoryPort categoryRepository;

  @InjectMocks private CategoryService categoryService;

  @Captor private ArgumentCaptor<CategoryDomain> categoryCaptor;

  private CategoryDomain existingCategory;

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

    var result = categoryService.create("FRUIT", "Fruits", "All kinds of fruits");

    assertNotNull(result);
    assertEquals("FRUIT", result.getId());
    assertEquals("Fruits", result.getName());
    assertEquals("All kinds of fruits", result.getDescription());
    assertEquals("ACTIVE", result.getStatus());
    verify(categoryRepository).save(any(CategoryDomain.class));
  }

  @Test
  @DisplayName("Should throw NotFoundException when category not found")
  void shouldThrowExceptionWhenCategoryNotFound() {
    when(categoryRepository.findById("NONEXISTENT")).thenReturn(null);

    var exception =
        assertThrows(NotFoundException.class, () -> categoryService.findById("NONEXISTENT"));
    assertEquals("CATEGORY_NOT_FOUND", exception.getErrorCode());
  }

  @Test
  @DisplayName("Should update an existing category")
  void shouldUpdateExistingCategory() {
    when(categoryRepository.findById("FRUIT")).thenReturn(existingCategory);
    when(categoryRepository.save(any(CategoryDomain.class))).thenReturn(existingCategory);

    var result =
        categoryService.update("FRUIT", "Fresh Fruits", "Fresh and organic fruits", "INACTIVE");

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
  @DisplayName("Should throw NotFoundException when deleting non-existent category")
  void shouldThrowExceptionWhenDeletingNonExistentCategory() {
    when(categoryRepository.findById("NONEXISTENT")).thenReturn(null);

    var exception =
        assertThrows(NotFoundException.class, () -> categoryService.deleteById("NONEXISTENT"));
    assertEquals("CATEGORY_NOT_FOUND", exception.getErrorCode());
  }

  @Test
  @DisplayName("Should return all categories with pagination")
  void shouldReturnAllCategories() {
    var categories = List.of(existingCategory);
    var pageResponse = PageResponse.of(categories, 0, 10, categories.size());
    var pageRequest = new PageRequestDto(0, 10, "name", "asc");
    var criteria = new CategoryCriteria(pageRequest, "", "ACTIVE");
    when(categoryRepository.findAll(any(PageRequestDto.class), any(), any()))
        .thenReturn(pageResponse);

    var result = categoryService.findAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.totalElements());
    assertEquals("Fruits", result.content().get(0).getName());
    verify(categoryRepository).findAll(any(PageRequestDto.class), any(), any());
  }
}
