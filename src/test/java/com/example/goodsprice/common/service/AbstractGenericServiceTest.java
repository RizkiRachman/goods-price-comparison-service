package com.example.goodsprice.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractGenericServiceTest {

  @Mock private GenericRepositoryPort<String, Long> repository;

  private TestGenericService service;

  @BeforeEach
  void setUp() {
    service = new TestGenericService("TestEntity", "TEST_NOT_FOUND", repository);
  }

  @Test
  void shouldFindByIdReturnsEntity() {
    when(repository.findById(1L)).thenReturn("entity1");

    var result = service.findById(1L);

    assertEquals("entity1", result);
  }

  @Test
  void shouldFindByIdThrowsNotFoundWhenMissing() {
    when(repository.findById(999L)).thenReturn(null);

    var ex = assertThrows(NotFoundException.class, () -> service.findById(999L));
    assertEquals("TEST_NOT_FOUND", ex.getErrorCode());
  }

  @Test
  void shouldSaveAndReturnEntity() {
    when(repository.save("newEntity")).thenReturn("savedEntity");

    var result = service.save("newEntity");

    assertEquals("savedEntity", result);
  }

  @Test
  void shouldFindAll() {
    var pageRequest = new PageRequestDto(1, 20, "name", "asc");
    var expected = PageResponse.of(List.of("a", "b"), 1, 20, 2);
    when(repository.findAll(pageRequest, "search", "active")).thenReturn(expected);

    var result = service.findAll(pageRequest, "search", "active");

    assertEquals(expected, result);
  }

  @Test
  void shouldDeleteByIdDeletesWhenFound() {
    when(repository.findById(1L)).thenReturn("entity1");

    service.deleteById(1L);

    verify(repository).deleteById(1L);
  }

  @Test
  void shouldDeleteByIdThrowsNotFoundWhenMissing() {
    when(repository.findById(999L)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> service.deleteById(999L));
  }

  private static final class TestGenericService extends AbstractGenericService<String, Long> {

    private final GenericRepositoryPort<String, Long> repository;

    TestGenericService(
        String entityName,
        String notFoundErrorCode,
        GenericRepositoryPort<String, Long> repository) {
      super(entityName, notFoundErrorCode);
      this.repository = repository;
    }

    @Override
    protected GenericRepositoryPort<String, Long> getRepository() {
      return repository;
    }
  }
}
