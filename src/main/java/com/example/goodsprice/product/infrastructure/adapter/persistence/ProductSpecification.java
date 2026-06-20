package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.common.persistence.SpecificationBuilder;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSpecification {

  public static Specification<ProductEntity> fromCriteria(ProductSearchCriteria criteria) {
    return new SpecificationBuilder<ProductEntity>()
        .addSearchLike(criteria.getSearch(), "name", "category", "brand")
        .addEqual("category", criteria.getCategory())
        .addEqual("brand", criteria.getBrand())
        .addEqual("status", criteria.getStatus())
        .addIfPresent(
            criteria,
            ProductSearchCriteria::getProductIds,
            ids -> (root, query, cb) -> root.get("id").in(ids))
        .build();
  }
}
