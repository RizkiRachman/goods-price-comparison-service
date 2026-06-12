package com.example.goodsprice.common.persistence;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;

/**
 * Fluent builder for constructing JPA {@link Specification} instances from field-level criteria.
 *
 * <p>Each {@code add*()} method adds an AND-condition to the specification. Methods that accept a
 * {@link String} value skip null or blank values, producing no predicate.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * var spec = new SpecificationBuilder<MyEntity>()
 *     .addEqual("status", criteria.status())
 *     .addSearchLike(criteria.search(), "name", "description")
 *     .build();
 * }</pre>
 *
 * @param <T> the JPA entity type
 */
public class SpecificationBuilder<T> {

  private Specification<T> spec;

  /**
   * Adds a search-like predicate (LIKE %value%) across multiple fields, combined with OR. Fields
   * are matched case-insensitively. Skips null or blank values.
   *
   * @param value the search term
   * @param fields the entity field names to search
   * @return {@code this} for fluent chaining
   */
  public SpecificationBuilder<T> addSearchLike(String value, String... fields) {
    if (value == null || value.isBlank()) {
      return this;
    }
    var pattern = "%" + value.toLowerCase(Locale.ROOT) + "%";
    Specification<T> newSpec =
        (root, query, cb) -> {
          var likes = new ArrayList<Predicate>();
          for (var field : fields) {
            likes.add(cb.like(cb.lower(root.get(field)), pattern));
          }
          return cb.or(likes.toArray(new Predicate[0]));
        };
    return and(newSpec);
  }

  /**
   * Adds an equality predicate. Skips null or blank values.
   *
   * @param field the entity field name
   * @param value the value to match
   * @return {@code this} for fluent chaining
   */
  public SpecificationBuilder<T> addEqual(String field, String value) {
    if (value == null || value.isBlank()) {
      return this;
    }
    Specification<T> newSpec = (root, query, cb) -> cb.equal(root.get(field), value);
    return and(newSpec);
  }

  /**
   * Adds a case-insensitive equality predicate. Skips null or blank values.
   *
   * @param field the entity field name
   * @param value the value to match (will be lowercased)
   * @return {@code this} for fluent chaining
   */
  public SpecificationBuilder<T> addEqualIgnoreCase(String field, String value) {
    if (value == null || value.isBlank()) {
      return this;
    }
    var lowerValue = value.toLowerCase(Locale.ROOT);
    Specification<T> newSpec = (root, query, cb) -> cb.equal(cb.lower(root.get(field)), lowerValue);
    return and(newSpec);
  }

  /**
   * Adds a predicate if the extracted value from the criteria is non-null.
   *
   * <p>This enables generic criteria-based specification building:
   *
   * <pre>{@code
   * new SpecificationBuilder<MyEntity>()
   *     .addIfPresent(criteria, MyCriteria::getName,
   *         name -> (root, query, cb) -> cb.equal(root.get("name"), name))
   *     .build();
   * }</pre>
   *
   * @param criteria the criteria object
   * @param extractor function to extract the field value from criteria
   * @param specFactory factory creating a {@link Specification} from the extracted value
   * @param <C> the criteria type
   * @param <V> the value type
   * @return {@code this} for fluent chaining
   */
  public <C, V> SpecificationBuilder<T> addIfPresent(
      C criteria, Function<C, V> extractor, FieldSpecFactory<T, V> specFactory) {
    V value = extractor.apply(criteria);
    if (Objects.nonNull(value)) {
      return and(specFactory.create(value));
    }
    return this;
  }

  /**
   * Builds the final {@link Specification}. Returns a "match all" specification if no predicates
   * were added (meaning no filtering).
   *
   * @return the composed specification
   */
  public Specification<T> build() {
    return spec != null ? spec : (root, query, cb) -> cb.conjunction();
  }

  private SpecificationBuilder<T> and(Specification<T> other) {
    spec = (spec == null) ? other : spec.and(other);
    return this;
  }

  /**
   * Functional interface for creating a {@link Specification} from a field value.
   *
   * @param <T> the JPA entity type
   * @param <V> the value type
   */
  @FunctionalInterface
  public interface FieldSpecFactory<T, V> {
    Specification<T> create(V value);
  }
}
