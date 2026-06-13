package com.example.goodsprice.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.goodsprice.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Abstract base test class for services extending {@link AbstractGenericService}.
 *
 * <p>Subclasses must implement the abstract hooks to provide:
 *
 * <ul>
 *   <li>Service instance and test IDs
 *   <li>Mock setup for repository operations
 *   <li>Invocation of service methods (findById, deleteById)
 *   <li>Verification of delete calls
 * </ul>
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

  // ---- Abstract hooks for test data ----

  /** The service under test. */
  protected abstract Object getService();

  /** An ID that exists in the repository. */
  protected abstract Object getExistingId();

  /** An ID that does NOT exist in the repository. */
  protected abstract Object getNonExistentId();

  /** The entity returned for the existing ID. */
  protected abstract Object getExistingEntity();

  /** The expected error code for NotFoundException. */
  protected abstract String getNotFoundErrorCode();

  // ---- Abstract hooks for mock setup ----

  /** Set up repository mock to return {@link #getExistingEntity()} for {@link #getExistingId()}. */
  protected abstract void mockFindByIdReturnsEntity();

  /** Set up repository mock to return null for {@link #getNonExistentId()}. */
  protected abstract void mockFindByIdReturnsNull();

  /**
   * Set up repository mock so that findById({@link #getExistingId()}) returns {@link
   * #getExistingEntity()}, making deleteById succeed.
   */
  protected abstract void mockDeleteByIdSucceeds();

  // ---- Abstract hooks for service method invocation ----

  /** Invoke findById on the service under test. */
  protected abstract Object invokeFindById(Object id);

  /** Invoke deleteById on the service under test. */
  protected abstract void invokeDeleteById(Object id);

  // ---- Abstract hooks for verification ----

  /** Verify that deleteById was called with the given id. */
  protected abstract void verifyDeleteByIdPerformed(Object id);

  /** Verify that deleteById was NOT called. */
  protected abstract void verifyDeleteByIdNotPerformed();

  // ======== Inherited test methods ========

  @Test
  void shouldFindByIdReturnsEntity() {
    mockFindByIdReturnsEntity();
    var result = invokeFindById(getExistingId());
    assertNotNull(result);
  }

  @Test
  void shouldFindByIdThrowsNotFoundWhenMissing() {
    mockFindByIdReturnsNull();
    var ex = assertThrows(NotFoundException.class, () -> invokeFindById(getNonExistentId()));
    assertEquals(getNotFoundErrorCode(), ex.getErrorCode());
  }

  @Test
  void shouldDeleteByIdDeletesWhenFound() {
    mockDeleteByIdSucceeds();
    invokeDeleteById(getExistingId());
    verifyDeleteByIdPerformed(getExistingId());
  }

  @Test
  void shouldDeleteByIdThrowsNotFoundWhenMissing() {
    mockFindByIdReturnsNull();
    assertThrows(NotFoundException.class, () -> invokeDeleteById(getNonExistentId()));
    verifyDeleteByIdNotPerformed();
  }
}
