package com.example.goodsprice.common.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@ExtendWith(MockitoExtension.class)
class PaginationHelperTest {

  @Mock private JpaSpecificationExecutor<String> executor;

  @Test
  void shouldConvertToPageable() {
    var request = new PageRequestDto(1, 20, "name", "asc");
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    var pageable = PageRequest.of(request.toZeroBased(), request.size(), sort);

    assertThat(pageable).isNotNull();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(20);
    assertThat(pageable.getSort().getOrderFor("name")).isNotNull();
    assertThat(pageable.getSort().getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
  }

  @Test
  void shouldHandleNullSortByAndDirection() {
    var request = new PageRequestDto(1, 20, null, null);
    Sort sort = Sort.by(Sort.Direction.ASC, "id");
    var pageable = PageRequest.of(request.toZeroBased(), request.size(), sort);

    assertThat(pageable).isNotNull();
    assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
    assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
  }

  @Test
  void shouldHandleEmptySortByAndDirection() {
    var request = new PageRequestDto(1, 20, "", "");
    Sort sort = Sort.by(Sort.Direction.ASC, "id");
    var pageable = PageRequest.of(request.toZeroBased(), request.size(), sort);

    assertThat(pageable).isNotNull();
    assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
    assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
  }

  @Test
  void shouldDefaultToIdSortWhenSortByIsNull() {
    var request = new PageRequestDto(1, 20, null, "asc");
    Sort sort = Sort.by(Sort.Direction.ASC, "id");
    var pageable = PageRequest.of(request.toZeroBased(), request.size(), sort);

    assertThat(pageable).isNotNull();
    assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
    assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
  }

  @Test
  void shouldDefaultToIdSortWhenSortByIsEmpty() {
    var request = new PageRequestDto(1, 20, "", "desc");
    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    var pageable = PageRequest.of(request.toZeroBased(), request.size(), sort);

    assertThat(pageable).isNotNull();
    assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
    assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.DESC);
  }

  @Test
  void shouldHandlePageSizeZeroOrLess() {
    var request = new PageRequestDto(1, 0, "name", "asc");
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    var pageable = PageRequest.of(request.toZeroBased(), request.size(), sort);

    assertThat(pageable).isNotNull();
    assertThat(pageable.getPageSize()).isEqualTo(20);
  }

  @Test
  void shouldHandlePageNumberLessThanOne() {
    var request = new PageRequestDto(0, 20, "name", "asc");
    Sort sort = Sort.by(Sort.Direction.ASC, "name");
    var pageable = PageRequest.of(request.toZeroBased(), request.size(), sort);

    assertThat(pageable).isNotNull();
    assertThat(pageable.getPageNumber()).isZero();
  }

  @Test
  void shouldFindAllWithSpec() {
    var request = new PageRequestDto(1, 20, "name", "asc");
    var entity = "entity";
    Page<String> page = new PageImpl<>(List.of(entity));
    when(executor.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

    PageResponse<String> result =
        PaginationHelper.findAll(request, (root, query, cb) -> cb.conjunction(), executor, s -> s);

    assertThat(result).isNotNull();
    assertThat(result.content()).containsExactly(entity);
    assertThat(result.page()).isEqualTo(1);
    assertThat(result.size()).isEqualTo(20);
  }

  @Test
  void shouldFindAllWithDescSort() {
    var request = new PageRequestDto(2, 10, "createdAt", "desc");
    Page<String> page = new PageImpl<>(List.of("a", "b"));
    when(executor.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

    PageResponse<String> result =
        PaginationHelper.findAll(request, (root, query, cb) -> cb.conjunction(), executor, s -> s);

    assertThat(result).isNotNull();
    assertThat(result.content()).hasSize(2);
    assertThat(result.page()).isEqualTo(2);
    assertThat(result.size()).isEqualTo(10);
  }

  @Test
  void shouldFindAllMapsEntitiesWithMapper() {
    var request = new PageRequestDto(1, 5, "id", "asc");
    Page<String> page = new PageImpl<>(List.of("e1", "e2"));
    when(executor.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

    PageResponse<Integer> result =
        PaginationHelper.findAll(
            request, (root, query, cb) -> cb.conjunction(), executor, String::length);

    assertThat(result.content()).containsExactly(2, 2);
  }

  @Test
  void shouldFindAllReturnsEmptyWhenNoResults() {
    var request = new PageRequestDto(1, 20, "name", "asc");
    Page<String> page = new PageImpl<>(List.of());
    when(executor.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

    PageResponse<String> result =
        PaginationHelper.findAll(request, (root, query, cb) -> cb.conjunction(), executor, s -> s);

    assertThat(result.content()).isEmpty();
    assertThat(result.totalElements()).isZero();
  }
}
