package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ActivityLogRepositoryAdapterDataJpaTest {

  @Autowired private JpaActivityLogRepository repository;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @DisplayName("Should persist and retrieve activity log with all fields")
  void shouldPersistAndRetrieveActivityLog() {
    var entity = new ActivityLogEntity();
    entity.setType(ActivityLogType.PRODUCT);
    entity.setAction(ActivityLogAction.CREATE);
    entity.setDescription("Created product with id 1");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId());

    assertTrue(found.isPresent());
    var log = found.get();
    assertNotNull(log.getId());
    assertEquals(ActivityLogType.PRODUCT, log.getType());
    assertEquals(ActivityLogAction.CREATE, log.getAction());
    assertEquals("Created product with id 1", log.getDescription());
  }

  @Test
  @DisplayName("Should persist activity log type enum values")
  void shouldPersistTypeEnum() {
    var receipt = new ActivityLogEntity();
    receipt.setType(ActivityLogType.RECEIPT);
    receipt.setAction(ActivityLogAction.CREATE);
    repository.saveAndFlush(receipt);

    var product = new ActivityLogEntity();
    product.setType(ActivityLogType.PRODUCT);
    product.setAction(ActivityLogAction.UPDATE);
    repository.saveAndFlush(product);

    var store = new ActivityLogEntity();
    store.setType(ActivityLogType.STORE);
    store.setAction(ActivityLogAction.CREATE);
    repository.saveAndFlush(store);
    entityManager.clear();

    assertEquals(
        ActivityLogType.RECEIPT, repository.findById(receipt.getId()).orElseThrow().getType());
    assertEquals(
        ActivityLogType.PRODUCT, repository.findById(product.getId()).orElseThrow().getType());
    assertEquals(ActivityLogType.STORE, repository.findById(store.getId()).orElseThrow().getType());
  }

  @Test
  @DisplayName("Should persist activity log action enum values")
  void shouldPersistActionEnum() {
    var create = new ActivityLogEntity();
    create.setType(ActivityLogType.ALERT);
    create.setAction(ActivityLogAction.CREATE);
    repository.saveAndFlush(create);

    var update = new ActivityLogEntity();
    update.setType(ActivityLogType.ALERT);
    update.setAction(ActivityLogAction.UPDATE);
    repository.saveAndFlush(update);
    entityManager.clear();

    assertEquals(
        ActivityLogAction.CREATE, repository.findById(create.getId()).orElseThrow().getAction());
    assertEquals(
        ActivityLogAction.UPDATE, repository.findById(update.getId()).orElseThrow().getAction());
  }

  @Test
  @DisplayName("Should persist timestamps")
  void shouldPersistTimestamps() {
    var entity = new ActivityLogEntity();
    entity.setType(ActivityLogType.PRODUCT);
    entity.setAction(ActivityLogAction.CREATE);
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId()).orElseThrow();

    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
  }

  @Test
  @DisplayName("Should delete activity log")
  void shouldDeleteActivityLog() {
    var entity = new ActivityLogEntity();
    entity.setType(ActivityLogType.PRODUCT);
    entity.setAction(ActivityLogAction.CREATE);
    repository.saveAndFlush(entity);
    entityManager.clear();

    repository.deleteById(entity.getId());
    entityManager.flush();

    assertTrue(repository.findById(entity.getId()).isEmpty());
  }
}
