package com.example.goodsprice.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class SpecificationBuilderTest {

  @Mock private Root<Object> root;

  @Mock private CriteriaBuilder cb;

  @Mock private Predicate predicate;

  @Test
  void addSearchLikeShouldAddLikePredicateWhenSearchIsNotBlank() {
    List<Predicate> predicates = new ArrayList<>();
    String search = "test";
    String[] fields = {"field1", "field2"};

    Path path1 = mock(Path.class);
    Path path2 = mock(Path.class);
    when(root.get("field1")).thenReturn(path1);
    when(root.get("field2")).thenReturn(path2);

    Path lowerPath1 = mock(Path.class);
    Path lowerPath2 = mock(Path.class);
    when(cb.lower(path1)).thenReturn(lowerPath1);
    when(cb.lower(path2)).thenReturn(lowerPath2);

    when(cb.like(lowerPath1, "%test%")).thenReturn(predicate);
    when(cb.like(lowerPath2, "%test%")).thenReturn(predicate);

    when(cb.or(any(Predicate[].class))).thenReturn(predicate);

    SpecificationBuilder.addSearchLike(predicates, root, cb, search, fields);

    assertThat(predicates).hasSize(1);
    verify(cb, times(fields.length)).lower(any(Path.class));
    verify(cb).or(any(Predicate[].class));
    verify(cb).like(lowerPath1, "%test%");
    verify(cb).like(lowerPath2, "%test%");
  }

  @Test
  void addSearchLikeShouldDoNothingWhenSearchIsBlank() {
    List<Predicate> predicates = new ArrayList<>();
    String search = " ";
    String[] fields = {"field1", "field2"};

    SpecificationBuilder.addSearchLike(predicates, root, cb, search, fields);

    assertThat(predicates).isEmpty();
    verifyNoInteractions(cb);
  }

  @Test
  void addSearchLikeShouldDoNothingWhenSearchIsNull() {
    List<Predicate> predicates = new ArrayList<>();
    String search = null;
    String[] fields = {"field1", "field2"};

    SpecificationBuilder.addSearchLike(predicates, root, cb, search, fields);

    assertThat(predicates).isEmpty();
    verifyNoInteractions(cb);
  }

  @Test
  void addEqualShouldAddEqualPredicateWhenValueIsNotBlank() {
    List<Predicate> predicates = new ArrayList<>();
    String field = "field";
    String value = "value";

    when(cb.equal(any(), anyString())).thenReturn(predicate);

    SpecificationBuilder.addEqual(predicates, root, cb, field, value);

    assertThat(predicates).hasSize(1);
    verify(cb).equal(root.get(field), value);
  }

  @Test
  void addEqualShouldDoNothingWhenValueIsBlank() {
    List<Predicate> predicates = new ArrayList<>();
    String field = "field";
    String value = " ";

    SpecificationBuilder.addEqual(predicates, root, cb, field, value);

    assertThat(predicates).isEmpty();
    verifyNoInteractions(cb);
  }

  @Test
  void addEqualShouldDoNothingWhenValueIsNull() {
    List<Predicate> predicates = new ArrayList<>();
    String field = "field";
    String value = null;

    SpecificationBuilder.addEqual(predicates, root, cb, field, value);

    assertThat(predicates).isEmpty();
    verifyNoInteractions(cb);
  }

  @Test
  void addEqualIgnoreCaseShouldAddEqualPredicateWhenValueIsNotBlank() {
    List<Predicate> predicates = new ArrayList<>();
    String field = "field";
    String value = "Value";
    String lowerCaseValue = value.toLowerCase(Locale.ROOT);

    Path path = mock(Path.class);
    when(root.get(field)).thenReturn(path);

    Path lowerPath = mock(Path.class);
    when(cb.lower(path)).thenReturn(lowerPath);

    when(cb.equal(eq(lowerPath), eq(lowerCaseValue))).thenReturn(predicate);

    SpecificationBuilder.addEqualIgnoreCase(predicates, root, cb, field, value);

    assertThat(predicates).hasSize(1);
    verify(cb).lower(root.get(field));
    verify(cb).equal(eq(lowerPath), eq(lowerCaseValue));
  }

  @Test
  void addEqualIgnoreCaseShouldDoNothingWhenValueIsBlank() {
    List<Predicate> predicates = new ArrayList<>();
    String field = "field";
    String value = " ";

    SpecificationBuilder.addEqualIgnoreCase(predicates, root, cb, field, value);

    assertThat(predicates).isEmpty();
    verifyNoInteractions(cb);
  }

  @Test
  void addEqualIgnoreCaseShouldDoNothingWhenValueIsNull() {
    List<Predicate> predicates = new ArrayList<>();
    String field = "field";
    String value = null;

    SpecificationBuilder.addEqualIgnoreCase(predicates, root, cb, field, value);

    assertThat(predicates).isEmpty();
    verifyNoInteractions(cb);
  }

  @Test
  void toArrayShouldConvertListToPredicateArray() {
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(predicate);

    Predicate[] array = SpecificationBuilder.toArray(predicates);

    assertThat(array).hasSize(1);
    assertThat(array[0]).isEqualTo(predicate);
  }
}
