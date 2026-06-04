package com.example.goodsprice.common.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

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

  private static final class TestRepositoryAdapter
      extends AbstractRepositoryAdapter<String, Long, String> {

    private final JpaRepository<String, Long> repository;

    TestRepositoryAdapter(JpaRepository<String, Long> repository) {
      this.repository = repository;
    }

    @Override
    protected JpaRepository<String, Long> getJpaRepository() {
      return repository;
    }

    @Override
    protected String toEntity(String domain) {
      return "entity";
    }

    @Override
    protected String toDomain(String entity) {
      return "MAPPED(" + entity + ")";
    }
  }
}
