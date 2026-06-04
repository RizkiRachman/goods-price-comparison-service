package com.example.goodsprice.product.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductSpecificationTest {

  @Mock private Root<ProductEntity> root;
  @Mock private CriteriaQuery<?> query;
  @Mock private CriteriaBuilder cb;

  @Test
  @DisplayName("Should build specification with search criteria")
  void shouldBuildSpecificationWithSearch() {
    var criteria = ProductSearchCriteria.builder().search("susu").page(0).size(20).build();

    Specification<ProductEntity> spec = ProductSpecification.fromCriteria(criteria);

    assertThat(spec).isNotNull();
  }

  @Test
  @DisplayName("Should build specification with category filter")
  void shouldBuildSpecificationWithCategory() {
    var criteria = ProductSearchCriteria.builder().category("Minuman").page(0).size(20).build();

    Specification<ProductEntity> spec = ProductSpecification.fromCriteria(criteria);

    assertThat(spec).isNotNull();
  }

  @Test
  @DisplayName("Should build specification with brand filter")
  void shouldBuildSpecificationWithBrand() {
    var criteria = ProductSearchCriteria.builder().brand("Indomilk").page(0).size(20).build();

    Specification<ProductEntity> spec = ProductSpecification.fromCriteria(criteria);

    assertThat(spec).isNotNull();
  }

  @Test
  @DisplayName("Should build specification with status filter")
  void shouldBuildSpecificationWithStatus() {
    var criteria = ProductSearchCriteria.builder().status("ACTIVE").page(0).size(20).build();

    Specification<ProductEntity> spec = ProductSpecification.fromCriteria(criteria);

    assertThat(spec).isNotNull();
  }

  @Test
  @DisplayName("Should build specification with product ids filter")
  void shouldBuildSpecificationWithProductIds() {
    var criteria =
        ProductSearchCriteria.builder().productIds(List.of(1L, 2L, 3L)).page(0).size(20).build();

    Specification<ProductEntity> spec = ProductSpecification.fromCriteria(criteria);

    assertThat(spec).isNotNull();
  }

  @Test
  @DisplayName("Should build specification with all filters combined")
  void shouldBuildSpecificationWithAllFilters() {
    var criteria =
        ProductSearchCriteria.builder()
            .search("susu")
            .category("Minuman")
            .brand("Indomilk")
            .status("ACTIVE")
            .productIds(List.of(1L, 2L))
            .page(0)
            .size(20)
            .build();

    Specification<ProductEntity> spec = ProductSpecification.fromCriteria(criteria);

    assertThat(spec).isNotNull();
  }

  @Test
  @DisplayName("Should build specification with no filters")
  void shouldBuildSpecificationWithNoFilters() {
    var criteria = ProductSearchCriteria.builder().page(0).size(20).build();

    Specification<ProductEntity> spec = ProductSpecification.fromCriteria(criteria);

    assertThat(spec).isNotNull();
  }
}
