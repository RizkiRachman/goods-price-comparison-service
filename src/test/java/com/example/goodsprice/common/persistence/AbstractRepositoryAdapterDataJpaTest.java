package com.example.goodsprice.common.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract base test class for DataJpaTest files using @SpringBootTest.
 *
 * <p>Provides common class annotations and EntityManager injection. Subclasses must implement
 * abstract hooks to provide their specific repository and entity creation methods.
 *
 * <p>Inherited infrastructure:
 *
 * <ul>
 *   <li>{@link #entityManager} — JPA EntityManager for persistence operations
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class AbstractRepositoryAdapterDataJpaTest {

  @PersistenceContext protected EntityManager entityManager;

  /**
   * Get the repository instance for the entity being tested.
   *
   * @return the repository instance
   */
  protected abstract Object getRepository();
}
