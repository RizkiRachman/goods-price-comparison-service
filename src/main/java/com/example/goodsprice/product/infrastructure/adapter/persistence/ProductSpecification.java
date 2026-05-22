package com.example.goodsprice.product.infrastructure.adapter.persistence;

import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSpecification {

  public static Specification<ProductEntity> fromCriteria(ProductSearchCriteria criteria) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();

      if (criteria.hasSearch()) {
        var pattern = "%" + criteria.getSearch().toLowerCase(Locale.ROOT) + "%";
        predicates.add(
            cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("category")), pattern),
                cb.like(cb.lower(root.get("brand")), pattern)));
      }
      if (criteria.hasCategory()) {
        predicates.add(cb.equal(root.get("category"), criteria.getCategory()));
      }
      if (criteria.hasBrand()) {
        predicates.add(cb.equal(root.get("brand"), criteria.getBrand()));
      }
      if (criteria.hasStatus()) {
        predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
