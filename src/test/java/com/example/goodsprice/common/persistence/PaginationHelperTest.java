package com.example.goodsprice.common.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.common.dto.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class PaginationHelperTest {

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
}
