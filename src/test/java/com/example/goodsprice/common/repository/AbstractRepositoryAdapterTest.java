package com.example.goodsprice.common.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.dto.PageRequestDto;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@ExtendWith(MockitoExtension.class)
class AbstractRepositoryAdapterTest {

  @Mock private JpaRepository<String, Long> jpaRepository;

  private TestRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new TestRepositoryAdapter(jpaRepository);
  }

  @Test
  void shouldSaveAndReturnDomain() {
    when(jpaRepository.save("entity")).thenReturn("savedEntity");

    var result = adapter.save("domain");

    assertEquals("MAPPED(savedEntity)", result);
  }

  @Test
  void shouldFindByIdReturnsDomainWhenFound() {
    when(jpaRepository.findById(1L)).thenReturn(Optional.of("entity1"));

    var result = adapter.findById(1L);

    assertEquals("MAPPED(entity1)", result);
  }

  @Test
  void shouldFindByIdReturnsNullWhenNotFound() {
    when(jpaRepository.findById(999L)).thenReturn(Optional.empty());

    assertNull(adapter.findById(999L));
  }

  @Test
  void shouldExistsByIdReturnsTrue() {
    when(jpaRepository.existsById(1L)).thenReturn(true);

    assertTrue(adapter.existsById(1L));
  }

  @Test
  void shouldExistsByIdReturnsFalse() {
    when(jpaRepository.existsById(2L)).thenReturn(false);

    assertFalse(adapter.existsById(2L));
  }

  @Test
  void shouldDeleteById() {
    adapter.deleteById(1L);

    verify(jpaRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Should save all entities and return mapped domain list")
  void shouldSaveAllEntities() {
    when(jpaRepository.saveAll(any())).thenReturn(List.of("savedEntity1", "savedEntity2"));

    var result = adapter.saveAll(List.of("domain1", "domain2"));

    assertEquals(2, result.size());
    assertEquals("MAPPED(savedEntity1)", result.get(0));
    assertEquals("MAPPED(savedEntity2)", result.get(1));
    verify(jpaRepository).saveAll(List.of("entity", "entity"));
  }

  @Test
  @DisplayName("Should return empty list when saveAll receives empty iterable")
  void shouldSaveAllReturnsEmptyList() {
    when(jpaRepository.saveAll(any())).thenReturn(List.of());

    var result = adapter.saveAll(List.of());

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should throw NullPointerException when saveAll receives null")
  void shouldSaveAllThrowsWhenNull() {
    assertThrows(NullPointerException.class, () -> adapter.saveAll(null));
  }

  @Test
  @DisplayName("Should return all mapped domains via deprecated findAll")
  void shouldFindAllDeprecatedReturnsAllDomains() {
    when(jpaRepository.findAll()).thenReturn(List.of("entity1", "entity2"));

    var result = adapter.findAll();

    assertEquals(2, result.size());
    assertEquals("MAPPED(entity1)", result.get(0));
    assertEquals("MAPPED(entity2)", result.get(1));
  }

  @Test
  @DisplayName("Should return empty list when deprecated findAll has no data")
  void shouldFindAllDeprecatedReturnsEmptyList() {
    when(jpaRepository.findAll()).thenReturn(List.of());

    var result = adapter.findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should return JpaSpecificationExecutor from cast")
  void shouldReturnJpaSpecificationExecutor() {
    @SuppressWarnings("unchecked")
    var repo =
        Mockito.mock(
            JpaRepository.class,
            Mockito.withSettings().extraInterfaces(JpaSpecificationExecutor.class));
    var testAdapter = new TestRepositoryAdapter(repo);

    var executor = testAdapter.jpaSpecificationExecutor();

    assertNotNull(executor);
  }

  @Test
  @DisplayName("Should return paginated results mapped to domains")
  void shouldFindAllPaginatedReturnsResults() {
    var pageRequest = new PageRequestDto(1, 10, "id", "asc");
    var page = new PageImpl<String>(List.of("entity1", "entity2"), PageRequest.of(0, 10), 2);
    when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);

    var result = adapter.findAll(pageRequest, "search", "status");

    assertEquals(2, result.content().size());
    assertEquals("MAPPED(entity1)", result.content().get(0));
    assertEquals("MAPPED(entity2)", result.content().get(1));
    assertEquals(1, result.page());
    assertEquals(10, result.size());
    assertEquals(2, result.totalElements());
    assertEquals(1, result.totalPages());
  }

  @Test
  @DisplayName("Should return empty page when no results found")
  void shouldFindAllPaginatedReturnsEmpty() {
    var pageRequest = new PageRequestDto(1, 10, "id", "asc");
    var page = new PageImpl<String>(List.of(), PageRequest.of(0, 10), 0);
    when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);

    var result = adapter.findAll(pageRequest, "search", "status");

    assertTrue(result.content().isEmpty());
    assertEquals(0, result.totalElements());
  }

  private static final class TestRepositoryAdapter
      extends AbstractRepositoryAdapter<String, Long, String> {

    TestRepositoryAdapter(JpaRepository<String, Long> repository) {
      super(repository, domain -> "entity", entity -> "MAPPED(" + entity + ")");
    }
  }
}
