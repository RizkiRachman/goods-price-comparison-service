package com.example.goodsprice.category.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import com.example.goodsprice.common.dto.PageRequestDto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryAdapterTest {

  @Mock private JpaCategoryRepository jpaRepository;
  @Mock private CategoryMapper mapper;

  private CategoryRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new CategoryRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  @DisplayName("Should save category")
  void shouldSaveCategory() {
    var domain = CategoryDomain.builder().id("FRUIT").name("Fruits").status("ACTIVE").build();
    var entity = new CategoryEntity();
    entity.setId("FRUIT");
    entity.setName("Fruits");

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepository.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.save(domain);

    assertNotNull(result);
    assertEquals("FRUIT", result.getId());
    verify(jpaRepository).save(entity);
  }

  @Test
  @DisplayName("Should find category by id")
  void shouldFindById() {
    var entity = new CategoryEntity();
    entity.setId("FRUIT");
    entity.setName("Fruits");
    var domain = CategoryDomain.builder().id("FRUIT").name("Fruits").build();

    when(jpaRepository.findById("FRUIT")).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findById("FRUIT");

    assertNotNull(result);
    assertEquals("FRUIT", result.getId());
  }

  @Test
  @DisplayName("Should return null when category not found")
  void shouldReturnNullWhenNotFound() {
    when(jpaRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

    assertNull(adapter.findById("NONEXISTENT"));
  }

  @Test
  @DisplayName("Should return null when id is null")
  void shouldReturnNullWhenIdIsNull() {
    assertNull(adapter.findById(null));
  }

  @Test
  @DisplayName("Should find all categories with pagination")
  void shouldFindAllWithPagination() {
    var pageRequest = new PageRequestDto(0, 10, "name", "asc");

    when(jpaRepository.findAll(
            any(org.springframework.data.jpa.domain.Specification.class),
            any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(org.springframework.data.domain.Page.empty());

    var result = adapter.findAll(pageRequest, null, null);

    assertNotNull(result);
    assertTrue(result.content().isEmpty());
  }

  @Test
  @DisplayName("Should save and update category")
  void shouldSaveAndUpdate() {
    var domain = CategoryDomain.builder().id("FRUIT").name("Fruits").status("ACTIVE").build();
    var entity = new CategoryEntity();

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepository.save(entity)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(domain);

    var saved = adapter.save(domain);
    assertNotNull(saved);

    // Update
    var updatedDomain =
        CategoryDomain.builder().id("FRUIT").name("Fresh Fruits").status("INACTIVE").build();
    when(mapper.toEntity(updatedDomain)).thenReturn(entity);
    when(mapper.toDomain(entity)).thenReturn(updatedDomain);

    var updated = adapter.save(updatedDomain);

    assertNotNull(updated);
    assertEquals("Fresh Fruits", updated.getName());
  }
}
