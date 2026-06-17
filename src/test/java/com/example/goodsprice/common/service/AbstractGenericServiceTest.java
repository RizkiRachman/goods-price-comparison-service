package com.example.goodsprice.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Abstract base test class for services extending {@link AbstractGenericService}.
 *
 * <p>Subclasses implement 6 essential hooks to provide the service instance, test data, and
 * repository mock. The 4 inherited test methods use {@link #getRepository()} with standard Mockito
 * {@code when()} and {@code verify()} — no separate mock setup or invocation hooks needed.
 *
 * <p>Inherited test methods:
 *
 * <ul>
 *   <li>{@link #shouldFindByIdReturnsEntity()} — findById with existing ID returns entity
 *   <li>{@link #shouldFindByIdThrowsNotFoundWhenMissing()} — findById with missing ID throws
 *       NotFoundException
 *   <li>{@link #shouldDeleteByIdDeletesWhenFound()} — deleteById with existing ID succeeds
 *   <li>{@link #shouldDeleteByIdThrowsNotFoundWhenMissing()} — deleteById with missing ID throws
 *       NotFoundException
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractGenericServiceTest {

  // ---- Essential abstract hooks ----

  /** The service under test. */
  protected abstract AbstractGenericService getService();

  /** An ID that exists in the repository. */
  protected abstract Object getExistingId();

  /** An ID that does NOT exist in the repository. */
  protected abstract Object getNonExistentId();

  /** The entity returned for the existing ID. */
  protected abstract Object getExistingEntity();

  /** The expected error code for NotFoundException. */
  protected abstract String getNotFoundErrorCode();

  /** The mock repository (must be a Mockito mock extending {@link GenericRepositoryPort}). */
  protected abstract GenericRepositoryPort getRepository();

  // ======== Inherited test methods ========

  @Test
  void shouldFindByIdReturnsEntity() {
    var repo = getRepository();
    when(repo.findById(getExistingId())).thenReturn(getExistingEntity());
    var result = getService().findById(getExistingId());
    assertNotNull(result);
  }

  @Test
  void shouldFindByIdThrowsNotFoundWhenMissing() {
    var repo = getRepository();
    when(repo.findById(getNonExistentId())).thenReturn(null);
    var ex = assertThrows(NotFoundException.class, () -> getService().findById(getNonExistentId()));
    assertEquals(getNotFoundErrorCode(), ex.getErrorCode());
  }

  @Test
  void shouldDeleteByIdDeletesWhenFound() {
    var repo = getRepository();
    when(repo.findById(getExistingId())).thenReturn(getExistingEntity());
    getService().deleteById(getExistingId());
    verify(repo).deleteById(getExistingId());
  }

  @Test
  void shouldDeleteByIdThrowsNotFoundWhenMissing() {
    var repo = getRepository();
    when(repo.findById(getNonExistentId())).thenReturn(null);
    assertThrows(NotFoundException.class, () -> getService().deleteById(getNonExistentId()));
    verify(repo, never()).deleteById(getNonExistentId());
  }

  @Test
  void shouldUpdateEntity() {
    var repo = getRepository();
    when(repo.findById(getExistingId())).thenReturn(getExistingEntity());
    when(repo.save(any())).thenReturn(getExistingEntity());

    var result =
        getService().update(getExistingId(), (existing, update) -> {}, getExistingEntity());

    assertNotNull(result);
    verify(repo).findById(getExistingId());
    verify(repo).save(any());
  }
}
