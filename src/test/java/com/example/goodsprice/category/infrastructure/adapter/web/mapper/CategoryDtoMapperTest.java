package com.example.goodsprice.category.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.api.model.Category;
import com.example.goodsprice.category.application.domain.model.CategoryDomain;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class CategoryDtoMapperTest {

  private final CategoryDtoMapper mapper = new CategoryDtoMapper();

  @Test
  void shouldMapToApiCategory() {
    var domain =
        CategoryDomain.builder()
            .id("cat-1")
            .name("Electronics")
            .description("Electronic devices")
            .status("APPROVED")
            .build();

    Category result = mapper.toApiCategory(domain);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("cat-1");
    assertThat(result.getName()).isEqualTo("Electronics");
  }

  @Test
  void shouldReturnNullForNullInput() {
    assertThat(mapper.toApiCategory(null)).isNull();
  }
}
