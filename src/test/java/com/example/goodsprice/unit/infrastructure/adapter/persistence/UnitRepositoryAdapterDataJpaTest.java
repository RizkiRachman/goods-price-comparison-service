package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.common.persistence.AbstractRepositoryAdapterDataJpaTest;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import jakarta.persistence.EntityExistsException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UnitRepositoryAdapterDataJpaTest extends AbstractRepositoryAdapterDataJpaTest {

  @Autowired private JpaUnitRepository repository;

  @Override
  protected Object getRepository() {
    return repository;
  }

  @Test
  @DisplayName("Should persist and retrieve unit with all fields")
  void shouldPersistAndRetrieveUnit() {
    var entity = new UnitEntity("KG", "Kilogram", "kg", UnitType.WEIGHT, "Unit of mass", "ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById("KG");

    assertTrue(found.isPresent());
    var unit = found.get();
    assertEquals("KG", unit.getId());
    assertEquals("Kilogram", unit.getName());
    assertEquals("kg", unit.getSymbol());
    assertEquals(UnitType.WEIGHT, unit.getType());
    assertEquals("Unit of mass", unit.getDescription());
    assertEquals("ACTIVE", unit.getStatus());
  }

  @Test
  @DisplayName("Should enforce unique id constraint")
  void shouldEnforceUniqueId() {
    var entity = new UnitEntity("LTR", "Liter", "L", UnitType.VOLUME, null, "ACTIVE");
    entityManager.persist(entity);
    entityManager.flush();

    var duplicate = new UnitEntity("LTR", "Liter Duplicate", "l", UnitType.VOLUME, null, "ACTIVE");
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
    Optional<UnitEntity> found = repository.findById("NONEXISTENT");

    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should delete unit")
  void shouldDeleteUnit() {
    var entity = new UnitEntity("DEL", "Delete Me", "D", UnitType.QUANTITY, null, "ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    repository.deleteById("DEL");
    entityManager.flush();

    assertTrue(repository.findById("DEL").isEmpty());
  }

  @Test
  @DisplayName("Should persist unit type enum values")
  void shouldPersistUnitType() {
    repository.saveAndFlush(new UnitEntity("W", "Weight", "w", UnitType.WEIGHT, null, "ACTIVE"));
    repository.saveAndFlush(new UnitEntity("V", "Volume", "v", UnitType.VOLUME, null, "ACTIVE"));
    repository.saveAndFlush(
        new UnitEntity("Q", "Quantity", "qty", UnitType.QUANTITY, null, "ACTIVE"));
    entityManager.clear();

    assertEquals(UnitType.WEIGHT, repository.findById("W").orElseThrow().getType());
    assertEquals(UnitType.VOLUME, repository.findById("V").orElseThrow().getType());
    assertEquals(UnitType.QUANTITY, repository.findById("Q").orElseThrow().getType());
  }
}
