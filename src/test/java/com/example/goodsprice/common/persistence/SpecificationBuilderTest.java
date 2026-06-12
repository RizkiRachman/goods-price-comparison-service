package com.example.goodsprice.common.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class SpecificationBuilderTest {

  @Mock private Root<Object> root;

  @Mock private CriteriaQuery<?> query;

  @Mock private CriteriaBuilder cb;

  @Mock private Predicate predicate;

  // ---------------------------------------------------------------------------
  // addSearchLike
  // ---------------------------------------------------------------------------

  @Nested
  @DisplayName("addSearchLike")
  class AddSearchLike {

    @Test
    @DisplayName("should skip and return this when value is null")
    void shouldSkipWhenValueIsNull() {
      var builder = new SpecificationBuilder<Object>();
      var result = builder.addSearchLike(null, "name");

      assertThat(result).isSameAs(builder);
      // build() should return a conjunction — no predicates accumulated
      Specification<Object> spec = builder.build();
      when(cb.conjunction()).thenReturn(predicate);
      assertThat(spec.toPredicate(root, query, cb)).isEqualTo(predicate);
    }

    @Test
    @DisplayName("should skip and return this when value is empty string")
    void shouldSkipWhenValueIsEmpty() {
      var builder = new SpecificationBuilder<Object>();
      var result = builder.addSearchLike("", "name");

      assertThat(result).isSameAs(builder);
      verifyNoInteractions(cb);
    }

    @Test
    @DisplayName("should skip and return this when value is whitespace only")
    void shouldSkipWhenValueIsWhitespace() {
      var builder = new SpecificationBuilder<Object>();
      var result = builder.addSearchLike("  ", "name");

      assertThat(result).isSameAs(builder);
      verifyNoInteractions(cb);
    }

    @Test
    @DisplayName("should add OR predicate for a single field")
    void shouldAddOrPredicateForSingleField() {
      Path path = mock(Path.class);
      Path lowerPath = mock(Path.class);
      when(root.get("name")).thenReturn(path);
      when(cb.lower(path)).thenReturn(lowerPath);
      when(cb.like(lowerPath, "%test%")).thenReturn(predicate);
      when(cb.or(any(Predicate[].class))).thenReturn(predicate);

      var spec = new SpecificationBuilder<Object>().addSearchLike("test", "name").build();
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(root).get("name");
      verify(cb).lower(path);
      verify(cb).like(lowerPath, "%test%");
      verify(cb).or(any(Predicate[].class));
    }

    @Test
    @DisplayName("should add OR predicate across multiple fields")
    void shouldAddOrPredicateForMultipleFields() {
      Path path1 = mock(Path.class);
      Path path2 = mock(Path.class);
      Path lowerPath1 = mock(Path.class);
      Path lowerPath2 = mock(Path.class);
      when(root.get("field1")).thenReturn(path1);
      when(root.get("field2")).thenReturn(path2);
      when(cb.lower(path1)).thenReturn(lowerPath1);
      when(cb.lower(path2)).thenReturn(lowerPath2);
      when(cb.like(lowerPath1, "%search%")).thenReturn(predicate);
      when(cb.like(lowerPath2, "%search%")).thenReturn(predicate);
      when(cb.or(any(Predicate[].class))).thenReturn(predicate);

      var spec =
          new SpecificationBuilder<Object>().addSearchLike("search", "field1", "field2").build();
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(root).get("field1");
      verify(root).get("field2");
      verify(cb).like(lowerPath1, "%search%");
      verify(cb).like(lowerPath2, "%search%");
      verify(cb).or(any(Predicate[].class));
    }

    @Test
    @DisplayName("should lower-case the search pattern")
    void shouldLowerCaseSearchPattern() {
      Path path = mock(Path.class);
      Path lowerPath = mock(Path.class);
      when(root.get("name")).thenReturn(path);
      when(cb.lower(path)).thenReturn(lowerPath);
      when(cb.like(lowerPath, "%mixed%")).thenReturn(predicate);
      when(cb.or(any(Predicate[].class))).thenReturn(predicate);

      var spec = new SpecificationBuilder<Object>().addSearchLike("Mixed", "name").build();
      spec.toPredicate(root, query, cb);

      // The pattern should be lower-cased: "Mixed" → "%mixed%"
      verify(cb).like(lowerPath, "%mixed%");
    }
  }

  // ---------------------------------------------------------------------------
  // addEqual
  // ---------------------------------------------------------------------------

  @Nested
  @DisplayName("addEqual")
  class AddEqual {

    @Test
    @DisplayName("should skip and return this when value is null")
    void shouldSkipWhenValueIsNull() {
      var builder = new SpecificationBuilder<Object>();
      var result = builder.addEqual("status", null);

      assertThat(result).isSameAs(builder);
      verifyNoInteractions(cb);
    }

    @Test
    @DisplayName("should skip and return this when value is blank")
    void shouldSkipWhenValueIsBlank() {
      var builder = new SpecificationBuilder<Object>();
      assertThat(builder.addEqual("status", "")).isSameAs(builder);
      assertThat(builder.addEqual("status", "  ")).isSameAs(builder);
      verifyNoInteractions(cb);
    }

    @Test
    @DisplayName("should add equality predicate for a valid value")
    void shouldAddEqualityPredicate() {
      when(root.get("status")).thenReturn(mock(Path.class));
      when(cb.equal(root.get("status"), "ACTIVE")).thenReturn(predicate);

      var spec = new SpecificationBuilder<Object>().addEqual("status", "ACTIVE").build();
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(cb).equal(root.get("status"), "ACTIVE");
    }
  }

  // ---------------------------------------------------------------------------
  // addEqualIgnoreCase
  // ---------------------------------------------------------------------------

  @Nested
  @DisplayName("addEqualIgnoreCase")
  class AddEqualIgnoreCase {

    @Test
    @DisplayName("should skip and return this when value is null")
    void shouldSkipWhenValueIsNull() {
      var builder = new SpecificationBuilder<Object>();
      var result = builder.addEqualIgnoreCase("name", null);

      assertThat(result).isSameAs(builder);
      verifyNoInteractions(cb);
    }

    @Test
    @DisplayName("should skip and return this when value is blank")
    void shouldSkipWhenValueIsBlank() {
      var builder = new SpecificationBuilder<Object>();
      assertThat(builder.addEqualIgnoreCase("name", "")).isSameAs(builder);
      assertThat(builder.addEqualIgnoreCase("name", "  ")).isSameAs(builder);
      verifyNoInteractions(cb);
    }

    @Test
    @DisplayName("should add case-insensitive equality predicate")
    void shouldAddCaseInsensitiveEqualityPredicate() {
      Path path = mock(Path.class);
      Path lowerPath = mock(Path.class);
      when(root.get("name")).thenReturn(path);
      when(cb.lower(path)).thenReturn(lowerPath);
      when(cb.equal(lowerPath, "testvalue")).thenReturn(predicate);

      var spec = new SpecificationBuilder<Object>().addEqualIgnoreCase("name", "TestValue").build();
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(root).get("name");
      verify(cb).lower(path);
      verify(cb).equal(lowerPath, "testvalue");
    }

    @Test
    @DisplayName("should lower-case the value for comparison")
    void shouldLowerCaseValue() {
      Path path = mock(Path.class);
      Path lowerPath = mock(Path.class);
      when(root.get("email")).thenReturn(path);
      when(cb.lower(path)).thenReturn(lowerPath);
      when(cb.equal(lowerPath, "user@example.com")).thenReturn(predicate);

      var spec =
          new SpecificationBuilder<Object>()
              .addEqualIgnoreCase("email", "USER@Example.COM")
              .build();
      spec.toPredicate(root, query, cb);

      // Verify the value was lowered
      ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
      verify(cb).equal(eq(lowerPath), captor.capture());
      assertThat(captor.getValue()).isEqualTo("user@example.com");
    }
  }

  // ---------------------------------------------------------------------------
  // addIfPresent
  // ---------------------------------------------------------------------------

  @Nested
  @DisplayName("addIfPresent")
  class AddIfPresent {

    @Test
    @DisplayName("should skip when extractor returns null")
    void shouldSkipWhenExtractorReturnsNull() {
      var builder = new SpecificationBuilder<Object>();
      var result =
          builder.addIfPresent(
              "someCriteria",
              c -> null,
              value -> {
                throw new AssertionError("factory should not be called");
              });

      assertThat(result).isSameAs(builder);
      verifyNoInteractions(cb);
    }

    @Test
    @DisplayName("should add predicate via factory when extractor returns non-null")
    void shouldAddPredicateWhenValueIsPresent() {
      Specification<Object> mockSpec = (r, q, cb) -> predicate;

      var builder = new SpecificationBuilder<Object>();
      builder.addIfPresent("criteria", c -> "value", val -> mockSpec);

      Specification<Object> spec = builder.build();
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
    }

    @Test
    @DisplayName("should chain with addIfPresent for multiple criteria")
    void shouldChainMultipleCriteria() {
      var builder = new SpecificationBuilder<Object>();
      Specification<Object> spec1 = (r, q, cb) -> predicate;
      Specification<Object> spec2 = (r, q, cb) -> predicate;

      // Add first criteria (non-null) — this sets the base spec
      builder.addIfPresent("criteria1", c -> "val1", val -> spec1);
      // Add second criteria (non-null) — this AND-combines with first
      builder.addIfPresent("criteria2", c -> "val2", val -> spec2);

      // Now build and verify that both predicates were combined via AND
      Specification<Object> built = builder.build();
      when(cb.and(predicate, predicate)).thenReturn(predicate);

      Predicate result = built.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(cb).and(predicate, predicate);
    }
  }

  // ---------------------------------------------------------------------------
  // build()
  // ---------------------------------------------------------------------------

  @Nested
  @DisplayName("build")
  class Build {

    @Test
    @DisplayName("should return conjunction when no predicates added")
    void shouldReturnConjunctionWhenNoPredicates() {
      var builder = new SpecificationBuilder<Object>();
      Specification<Object> spec = builder.build();

      when(cb.conjunction()).thenReturn(predicate);
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(cb).conjunction();
    }

    @Test
    @DisplayName("should return stored specification when predicates exist")
    void shouldReturnSpecWhenPredicatesExist() {
      // Add one predicate then build
      Path path = mock(Path.class);
      when(root.get("name")).thenReturn(path);
      when(cb.equal(path, "test")).thenReturn(predicate);

      var spec = new SpecificationBuilder<Object>().addEqual("name", "test").build();
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(cb).equal(root.get("name"), "test");
      // conjunction must NOT be called when spec is non-null
      verify(cb, never()).conjunction();
    }
  }

  // ---------------------------------------------------------------------------
  // Chaining — Integration
  // ---------------------------------------------------------------------------

  @Nested
  @DisplayName("chaining")
  class Chaining {

    @Test
    @DisplayName("should AND-combine addSearchLike and addEqual")
    void shouldCombineSearchLikeAndEqual() {
      // Setup for searchLike → "field1" with "search"
      Path path1 = mock(Path.class);
      Path lowerPath1 = mock(Path.class);
      when(root.get("field1")).thenReturn(path1);
      when(cb.lower(path1)).thenReturn(lowerPath1);
      when(cb.like(lowerPath1, "%search%")).thenReturn(predicate);
      when(cb.or(any(Predicate[].class))).thenReturn(predicate);

      // Setup for addEqual → "status" = "ACTIVE"
      Path path2 = mock(Path.class);
      when(root.get("status")).thenReturn(path2);
      Predicate statusPredicate = mock(Predicate.class);
      when(cb.equal(path2, "ACTIVE")).thenReturn(statusPredicate);

      // The AND combination
      Predicate andPredicate = mock(Predicate.class);
      when(cb.and(predicate, statusPredicate)).thenReturn(andPredicate);

      var spec =
          new SpecificationBuilder<Object>()
              .addSearchLike("search", "field1")
              .addEqual("status", "ACTIVE")
              .build();

      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(andPredicate);
      verify(cb).and(predicate, statusPredicate);
    }

    @Test
    @DisplayName("should AND-combine addEqualIgnoreCase and addEqual")
    void shouldCombineIgnoreCaseAndEqual() {
      // Setup for ignore-case equal on "name"
      Path namePath = mock(Path.class);
      Path lowerNamePath = mock(Path.class);
      when(root.get("name")).thenReturn(namePath);
      when(cb.lower(namePath)).thenReturn(lowerNamePath);
      when(cb.equal(lowerNamePath, "alice")).thenReturn(predicate);

      // Setup for addEqual on "status"
      Path statusPath = mock(Path.class);
      when(root.get("status")).thenReturn(statusPath);
      Predicate statusPredicate = mock(Predicate.class);
      when(cb.equal(statusPath, "ACTIVE")).thenReturn(statusPredicate);

      Predicate andPredicate = mock(Predicate.class);
      when(cb.and(predicate, statusPredicate)).thenReturn(andPredicate);

      var spec =
          new SpecificationBuilder<Object>()
              .addEqualIgnoreCase("name", "Alice")
              .addEqual("status", "ACTIVE")
              .build();

      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(andPredicate);
      verify(cb).and(predicate, statusPredicate);
    }

    @Test
    @DisplayName("should chain all methods together")
    void shouldChainAllMethods() {
      // addSearchLike("search", "field1", "field2")
      Path path1 = mock(Path.class);
      Path path2 = mock(Path.class);
      Path lowerPath1 = mock(Path.class);
      Path lowerPath2 = mock(Path.class);
      when(root.get("field1")).thenReturn(path1);
      when(root.get("field2")).thenReturn(path2);
      when(cb.lower(path1)).thenReturn(lowerPath1);
      when(cb.lower(path2)).thenReturn(lowerPath2);
      when(cb.like(lowerPath1, "%search%")).thenReturn(predicate);
      when(cb.like(lowerPath2, "%search%")).thenReturn(predicate);
      Predicate searchOrPredicate = mock(Predicate.class);
      when(cb.or(any(Predicate[].class))).thenReturn(searchOrPredicate);

      // addEqual("status", "ACTIVE")
      Path statusPath = mock(Path.class);
      when(root.get("status")).thenReturn(statusPath);
      Predicate statusPredicate = mock(Predicate.class);
      when(cb.equal(statusPath, "ACTIVE")).thenReturn(statusPredicate);

      // addEqualIgnoreCase("email", "User@Example.COM")
      Path emailPath = mock(Path.class);
      Path lowerEmailPath = mock(Path.class);
      when(root.get("email")).thenReturn(emailPath);
      when(cb.lower(emailPath)).thenReturn(lowerEmailPath);
      Predicate emailPredicate = mock(Predicate.class);
      when(cb.equal(lowerEmailPath, "user@example.com")).thenReturn(emailPredicate);

      // AND combinations
      Predicate firstAnd = mock(Predicate.class);
      when(cb.and(searchOrPredicate, statusPredicate)).thenReturn(firstAnd);
      Predicate finalAnd = mock(Predicate.class);
      when(cb.and(firstAnd, emailPredicate)).thenReturn(finalAnd);

      var spec =
          new SpecificationBuilder<Object>()
              .addSearchLike("search", "field1", "field2")
              .addEqual("status", "ACTIVE")
              .addEqualIgnoreCase("email", "User@Example.COM")
              .build();

      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(finalAnd);
      verify(cb).and(searchOrPredicate, statusPredicate);
      verify(cb).and(firstAnd, emailPredicate);
    }

    @Test
    @DisplayName("should handle null/blank values in chain without breaking")
    void shouldHandleNullAndBlankInChain() {
      // All nullable methods get null/blank — should build conjunction
      var spec =
          new SpecificationBuilder<Object>()
              .addSearchLike(null, "name")
              .addEqual("status", "")
              .addEqualIgnoreCase("email", "  ")
              .build();

      when(cb.conjunction()).thenReturn(predicate);
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(cb).conjunction();
    }
  }

  // ---------------------------------------------------------------------------
  // Edge cases
  // ---------------------------------------------------------------------------

  @Nested
  @DisplayName("edge cases")
  class EdgeCases {

    @Test
    @DisplayName("should handle empty fields array in addSearchLike with valid value")
    void shouldHandleEmptyFieldsArray() {
      // When fields is empty, the OR predicate receives an empty array
      // This is an edge case — cb.or(new Predicate[0]) may return a conjunction
      when(cb.or(any(Predicate[].class))).thenReturn(predicate);

      var spec = new SpecificationBuilder<Object>().addSearchLike("test").build();
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
      verify(cb).or(any(Predicate[].class));
    }

    @Test
    @DisplayName("should handle zero-length field name in addEqual")
    void shouldHandleEmptyFieldName() {
      // addEqual with blank value doesn't call cb, so this is about the
      // case where field name is blank but value is valid.
      // The Specification will try root.get("") which is JPA-defined behavior.
      when(root.get("")).thenReturn(mock(Path.class));
      when(cb.equal(root.get(""), "val")).thenReturn(predicate);

      var spec = new SpecificationBuilder<Object>().addEqual("", "val").build();
      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(predicate);
    }

    @Test
    @DisplayName("should build multiple addEqual calls AND-combined")
    void shouldChainMultipleAddEqual() {
      Path path1 = mock(Path.class);
      Path path2 = mock(Path.class);
      when(root.get("status")).thenReturn(path1);
      when(root.get("type")).thenReturn(path2);
      Predicate p1 = mock(Predicate.class);
      Predicate p2 = mock(Predicate.class);
      when(cb.equal(path1, "ACTIVE")).thenReturn(p1);
      when(cb.equal(path2, "PREMIUM")).thenReturn(p2);
      Predicate combined = mock(Predicate.class);
      when(cb.and(p1, p2)).thenReturn(combined);

      var spec =
          new SpecificationBuilder<Object>()
              .addEqual("status", "ACTIVE")
              .addEqual("type", "PREMIUM")
              .build();

      Predicate result = spec.toPredicate(root, query, cb);

      assertThat(result).isEqualTo(combined);
      verify(cb).and(p1, p2);
    }
  }
}
