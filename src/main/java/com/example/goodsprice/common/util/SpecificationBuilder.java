package com.example.goodsprice.common.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecificationBuilder {

  public static <T> void addSearchLike(
      List<Predicate> predicates,
      Root<T> root,
      CriteriaBuilder cb,
      String search,
      String... fields) {
    if (Objects.isNull(search) || search.isBlank()) {
      return;
    }
    var pattern = "%" + search.toLowerCase(Locale.ROOT) + "%";
    var likes = new ArrayList<Predicate>();
    for (var field : fields) {
      likes.add(cb.like(cb.lower(root.get(field)), pattern));
    }
    predicates.add(cb.or(likes.toArray(new Predicate[0])));
  }

  public static <T> void addEqual(
      List<Predicate> predicates, Root<T> root, CriteriaBuilder cb, String field, String value) {
    if (Objects.isNull(value) || value.isBlank()) {
      return;
    }
    predicates.add(cb.equal(root.get(field), value));
  }

  public static <T> void addEqualIgnoreCase(
      List<Predicate> predicates, Root<T> root, CriteriaBuilder cb, String field, String value) {
    if (Objects.isNull(value) || value.isBlank()) {
      return;
    }
    predicates.add(cb.equal(cb.lower(root.get(field)), value.toLowerCase(Locale.ROOT)));
  }

  public static Predicate[] toArray(List<Predicate> predicates) {
    return predicates.toArray(new Predicate[0]);
  }
}
