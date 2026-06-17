package com.example.goodsprice.common.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Pagination;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractCrudWebAdapterTest {

  private TestCrudWebAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new TestCrudWebAdapter();
  }

  @Test
  void shouldResolvePaginationWithDefaults() {
    var params = adapter.resolvePagination(null, null, null, null, "name", "asc");

    assertEquals(1, params.page());
    assertEquals(20, params.size());
    assertEquals("name", params.sortBy());
    assertEquals("asc", params.sortOrder());
  }

  @Test
  void shouldResolvePaginationWithProvidedValues() {
    var params = adapter.resolvePagination(3, 50, "price", "desc", "name", "asc");

    assertEquals(3, params.page());
    assertEquals(50, params.size());
    assertEquals("price", params.sortBy());
    assertEquals("desc", params.sortOrder());
  }

  @Test
  void shouldResolvePaginationClampsSize() {
    var params = adapter.resolvePagination(1, 500, "name", "asc", "name", "asc");

    assertEquals(100, params.size());
  }

  @Test
  void shouldResolvePaginationWithDefaultSortOrder() {
    var params = adapter.resolvePagination(null, null, null, null, "createdAt", "desc");

    assertEquals(1, params.page());
    assertEquals(20, params.size());
    assertEquals("createdAt", params.sortBy());
    assertEquals("desc", params.sortOrder());
  }

  @Test
  void shouldResolvePaginationWithCustomDefaultSortOrder() {
    var params = adapter.resolvePagination(2, 10, "price", "asc", "name", "asc");

    assertEquals(2, params.page());
    assertEquals(10, params.size());
    assertEquals("price", params.sortBy());
    assertEquals("asc", params.sortOrder());
  }

  @Test
  void shouldResolveStatusReturnsValueWhenNotNull() {
    assertEquals("approved", adapter.resolveStatus(EntityStatus.APPROVED));
  }

  @Test
  void shouldResolveStatusReturnsNullWhenNull() {
    assertNull(adapter.resolveStatus(null));
  }

  @Test
  void shouldResolveStatusReturnsValueForRejected() {
    assertEquals("rejected", adapter.resolveStatus(EntityStatus.REJECTED));
  }

  @Test
  void shouldResolvePaginationWithNegativePage() {
    var params = adapter.resolvePagination(-1, 20, "name", "asc", "name", "asc");

    assertEquals(1, params.page());
  }

  @Test
  void shouldResolvePaginationWithZeroSize() {
    var params = adapter.resolvePagination(1, 0, "name", "asc", "name", "asc");

    assertEquals(20, params.size());
  }

  @Test
  void shouldBuildPageRequestFromPaginationParams() {
    var params = new TestCrudWebAdapter.PaginationParams(2, 30, "name", "asc");
    var pageRequest = adapter.buildPageRequest(params);

    assertEquals(2, pageRequest.page());
    assertEquals(30, pageRequest.size());
    assertEquals("name", pageRequest.sortBy());
    assertEquals("asc", pageRequest.sortDirection());
  }

  @Test
  @DisplayName("Should build typed list response")
  void shouldBuildTypedListResponse() {
    var domains = List.of(new TestDomain("A"), new TestDomain("B"));
    var pageResponse = PageResponse.of(domains, 1, 10, 2);

    var result =
        adapter.buildTypedListResponse(pageResponse, TestDomain::name, TestListResponse::new);

    assertNotNull(result);
    assertEquals(List.of("A", "B"), result.getData());
    assertNotNull(result.getPagination());
    assertEquals(1, result.getPagination().getPage());
  }

  private record TestDomain(String name) {}

  private static class TestListResponse {
    private List<String> data;
    private Pagination pagination;

    public void setData(List<String> data) {
      this.data = data;
    }

    public List<String> getData() {
      return data;
    }

    public void setPagination(Pagination pagination) {
      this.pagination = pagination;
    }

    public Pagination getPagination() {
      return pagination;
    }
  }

  private static final class TestCrudWebAdapter extends AbstractCrudWebAdapter {
    // Exposes protected methods for testing
    @Override
    public PaginationParams resolvePagination(
        Integer page,
        Integer pageSize,
        String sortBy,
        String sortOrder,
        String defaultSort,
        String defaultSortOrder) {
      return super.resolvePagination(
          page, pageSize, sortBy, sortOrder, defaultSort, defaultSortOrder);
    }

    @Override
    public String resolveStatus(EntityStatus status) {
      return super.resolveStatus(status);
    }

    @Override
    public PageRequestDto buildPageRequest(PaginationParams params) {
      return super.buildPageRequest(params);
    }
  }
}
