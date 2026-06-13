package com.example.goodsprice.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.goodsprice.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Mixin interface for testing service-specific {@link NotFoundException} scenarios beyond the
 * generic findById/deleteById patterns covered by {@link AbstractGenericServiceTest}.
 *
 * <p>Use this for service methods that internally call findById (or equivalent) and should throw
 * NotFoundException when the entity is not found — e.g., update(), process(), approve(),
 * splitBill().
 *
 * <p>Implementations provide two hooks:
 *
 * <ul>
 *   <li>{@link #mockRepositoryReturnsNull()} — set up repository/port mock to return null
 *   <li>{@link #serviceMethodThatShouldThrowNotFound()} — executable that calls the service method
 * </ul>
 *
 * <p>The inherited test method verifies NotFoundException is thrown. Optionally override {@link
 * #getNotFoundErrorCode()} to verify the error code.
 */
public interface ServiceLayerNotFoundExceptionTest {

  /**
   * Set up repository or port mocks so that findById (or equivalent lookup) returns {@code null}
   * for the non-existent test ID.
   */
  void mockRepositoryReturnsNull();

  /**
   * Return an {@link Executable} that invokes the service method expected to throw {@link
   * NotFoundException}. Use a lambda:
   *
   * <pre>{@code
   * return () -> priceService.update(999L, updateDomain);
   * }</pre>
   */
  Executable serviceMethodThatShouldThrowNotFound();

  /**
   * The expected error code in the NotFoundException. Override to verify; default ({@code null})
   * skips error code verification.
   */
  default String getNotFoundErrorCode() {
    return null;
  }

  @Test
  default void shouldThrowNotFoundWhenEntityDoesNotExist() {
    mockRepositoryReturnsNull();
    var ex = assertThrows(NotFoundException.class, serviceMethodThatShouldThrowNotFound());
    if (getNotFoundErrorCode() != null) {
      assertEquals(getNotFoundErrorCode(), ex.getErrorCode());
    }
  }
}
