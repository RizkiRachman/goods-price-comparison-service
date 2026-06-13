package com.example.goodsprice.category.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategoryRepositoryAdapterDataJpaTest {

  @Autowired private JpaCategoryRepository repository;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @DisplayName("Should persist and retrieve category with all fields")
  void shouldPersistAndRetrieveCategory() {
    var entity = new CategoryEntity("FRUIT", "Fruits", "All kinds of fruits", "ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById("FRUIT");

    assertTrue(found.isPresent());
    var cat = found.get();
    assertEquals("FRUIT", cat.getId());
    assertEquals("Fruits", cat.getName());
    assertEquals("All kinds of fruits", cat.getDescription());
    assertEquals("ACTIVE", cat.getStatus());
  }

  @Test
  @DisplayName("Should enforce unique id constraint")
  void shouldEnforceUniqueId() {
    // First entity remains managed in persistence context (no clear() between),
    // so Hibernate detects duplicate at session level → EntityExistsException.
    // If clear() were added, it would be a DB-level DataIntegrityViolationException.
    entityManager.persist(new CategoryEntity("SAME", "First", null, "ACTIVE"));
    entityManager.flush();

    var duplicate = new CategoryEntity("SAME", "Second", null, "ACTIVE");
    assertThrows(
        EntityExistsException.class,
        () -> {
          entityManager.persist(duplicate);
          entityManager.flush();
        });
  }

  @Test
  @DisplayName("Should return empty for non-existent id")
  void shouldReturnEmptyForNonExistentId() {
    Optional<CategoryEntity> found = repository.findById("NONEXISTENT");
    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should delete category")
  void shouldDeleteCategory() {
    var entity = new CategoryEntity("DELETE", "Delete Me", null, "ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    repository.deleteById("DELETE");
    entityManager.flush();

    assertTrue(repository.findById("DELETE").isEmpty());
  }

  @Test
  @DisplayName("Should persist entity status values")
  void shouldPersistEntityStatus() {
    repository.saveAndFlush(new CategoryEntity("A", "Approved Cat", null, "APPROVED"));
    repository.saveAndFlush(new CategoryEntity("P", "Pending Cat", null, "PENDING"));
    repository.saveAndFlush(new CategoryEntity("R", "Rejected Cat", null, "REJECTED"));
    entityManager.clear();

    assertEquals("APPROVED", repository.findById("A").orElseThrow().getStatus());
    assertEquals("PENDING", repository.findById("P").orElseThrow().getStatus());
    assertEquals("REJECTED", repository.findById("R").orElseThrow().getStatus());
  }

  @Test
  @DisplayName("Should persist timestamps")
  void shouldPersistTimestamps() {
    var entity = new CategoryEntity("TS", "Timestamp Test", null, "ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById("TS").orElseThrow();

    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
  }
}
