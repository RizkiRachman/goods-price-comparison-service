package com.example.goodsprice.alert.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.alert.infrastructure.adapter.persistence.entity.AlertSubscriptionEntity;
import com.example.goodsprice.common.persistence.AbstractRepositoryAdapterDataJpaTest;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AlertRepositoryAdapterDataJpaTest extends AbstractRepositoryAdapterDataJpaTest {

  @Autowired private JpaAlertSubscriptionRepository repository;

  @Override
  protected Object getRepository() {
    return repository;
  }

  @Test
  @DisplayName("Should persist and retrieve alert subscription with all fields")
  void shouldPersistAndRetrieveAlertSubscription() {
    var entity =
        new AlertSubscriptionEntity(
            "sub-001", 1L, "Test Product", 15000.0, null, "EMAIL", "user@test.com", "ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById("sub-001");

    assertTrue(found.isPresent());
    var sub = found.get();
    assertEquals("sub-001", sub.getId());
    assertEquals(1L, sub.getProductId());
    assertEquals("Test Product", sub.getProductName());
    assertEquals(15000.0, sub.getTargetPrice(), 0.001);
    assertEquals("EMAIL", sub.getNotificationMethod());
    assertEquals("user@test.com", sub.getEmail());
    assertEquals("ACTIVE", sub.getStatus());
  }

  @Test
  @DisplayName("Should enforce unique id constraint")
  void shouldEnforceUniqueId() {
    entityManager.persist(
        new AlertSubscriptionEntity("same-id", 1L, "Product", 10000.0, null, null, null, "ACTIVE"));
    entityManager.flush();

    var duplicate =
        new AlertSubscriptionEntity(
            "same-id", 2L, "Product 2", 20000.0, null, null, null, "ACTIVE");
    assertThrows(
        EntityExistsException.class,
        () -> {
          entityManager.persist(duplicate);
          entityManager.flush();
        });
  }

  @Test
  @DisplayName("Should persist timestamps")
  void shouldPersistTimestamps() {
    var entity =
        new AlertSubscriptionEntity("ts-001", 1L, "Test", 10000.0, null, null, null, "ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById("ts-001").orElseThrow();

    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
  }

  @Test
  @DisplayName("Should delete alert subscription")
  void shouldDeleteAlertSubscription() {
    var entity =
        new AlertSubscriptionEntity(
            "del-001", 1L, "Delete Me", 10000.0, null, null, null, "ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    repository.deleteById("del-001");
    entityManager.flush();

    assertTrue(repository.findById("del-001").isEmpty());
  }
}
