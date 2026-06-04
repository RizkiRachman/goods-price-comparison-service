package com.example.goodsprice.common.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.api.model.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCrudWebAdapterTest {

  private TestCrudWebAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new TestCrudWebAdapter();
  }

  @Test
  void shouldResolvePaginationWithDefaults() {
    var params = adapter.resolvePagination(null, null, null, null, "name");

    assertEquals(1, params.page());
    assertEquals(20, params.size());
    assertEquals("name", params.sortBy());
    assertEquals("asc", params.sortOrder());
  }

  @Test
  void shouldResolvePaginationWithProvidedValues() {
    var params = adapter.resolvePagination(3, 50, "price", "desc", "name");

    assertEquals(3, params.page());
    assertEquals(50, params.size());
    assertEquals("price", params.sortBy());
    assertEquals("desc", params.sortOrder());
  }

  @Test
  void shouldResolvePaginationClampsSize() {
    var params = adapter.resolvePagination(1, 500, "name", "asc", "name");

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
    var params = adapter.resolvePagination(2, 10, "price", "asc", "name");

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
    var params = adapter.resolvePagination(-1, 20, "name", "asc", "name");

    assertEquals(1, params.page());
  }

  @Test
  void shouldResolvePaginationWithZeroSize() {
    var params = adapter.resolvePagination(1, 0, "name", "asc", "name");

    assertEquals(20, params.size());
  }

  private static final class TestCrudWebAdapter extends AbstractCrudWebAdapter {
    // Exposes protected methods for testing
    @Override
    public PaginationParams resolvePagination(
        Integer page, Integer pageSize, String sortBy, String sortOrder, String defaultSort) {
      return super.resolvePagination(page, pageSize, sortBy, sortOrder, defaultSort);
    }

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
  }
}
