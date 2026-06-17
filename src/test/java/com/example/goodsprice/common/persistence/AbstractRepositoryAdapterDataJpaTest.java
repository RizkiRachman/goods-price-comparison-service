package com.example.goodsprice.common.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  protected abstract <T> T getRepository();

  /**
   * Asserts that an entity can be persisted and retrieved by ID.
   *
   * @param entity the entity to persist
   * @param entityClass the entity class type
   * @param id the expected ID after persist (may be generated)
   * @param <E> entity type
   * @param <ID> ID type
   */
  protected <E, ID> void assertPersistAndRetrieve(E entity, Class<E> entityClass, ID id) {
    entityManager.persist(entity);
    entityManager.flush();
    var found = entityManager.find(entityClass, id);
    assertNotNull(found, "Entity should be retrievable after persist");
  }

  /**
   * Asserts that a unique constraint violation throws the expected exception.
   *
   * @param entity1 first entity to persist (succeeds)
   * @param entity2 second entity with duplicate unique field (fails)
   */
  protected void assertUniqueConstraintViolation(Runnable entity1, Runnable entity2) {
    entity1.run();
    entityManager.flush();
    assertThrows(
        Exception.class,
        () -> {
          entity2.run();
          entityManager.flush();
        });
  }

  /**
   * Asserts that an entity can be deleted by removing it from persistence context.
   *
   * @param entity the entity to persist then delete
   * @param entityClass the entity class type
   * @param id the entity ID
   * @param <E> entity type
   * @param <ID> ID type
   */
  protected <E, ID> void assertDelete(E entity, Class<E> entityClass, ID id) {
    entityManager.persist(entity);
    entityManager.flush();
    var managed = entityManager.find(entityClass, id);
    assertNotNull(managed, "Entity should exist before delete");
    entityManager.remove(managed);
    entityManager.flush();
    var afterDelete = entityManager.find(entityClass, id);
    assertNull(afterDelete, "Entity should not exist after delete");
  }
}
