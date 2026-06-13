package com.example.goodsprice.store.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.common.persistence.AbstractRepositoryAdapterDataJpaTest;
import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StoreRepositoryAdapterDataJpaTest extends AbstractRepositoryAdapterDataJpaTest {

  @Autowired private JpaStoreRepository repository;

  @Override
  protected Object getRepository() {
    return repository;
  }

  @Test
  @DisplayName("Should persist and retrieve store with all fields")
  void shouldPersistAndRetrieveStore() {
    var entity = new StoreEntity("Toko Makmur", "Jakarta");
    entity.setChain("Makmur Group");
    entity.setAddress("Jl. Merdeka No. 1");
    entity.setLatitude(-6.2088);
    entity.setLongitude(106.8456);
    entity.setStatus("ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId());

    assertTrue(found.isPresent());
    var store = found.get();
    assertEquals("Toko Makmur", store.getName());
    assertEquals("Jakarta", store.getLocation());
  }

  @Test
  @DisplayName("Should find stores by name")
  void shouldFindByName() {
    repository.saveAndFlush(new StoreEntity("Store A", "Location A"));
    repository.saveAndFlush(new StoreEntity("Store B", "Location B"));
    entityManager.clear();

    List<StoreEntity> results = repository.findByName("Store A");

    assertEquals(1, results.size());
    assertEquals("Store A", results.get(0).getName());
  }

  @Test
  @DisplayName("Should return empty list when name not found")
  void shouldReturnEmptyListWhenNameNotFound() {
    repository.saveAndFlush(new StoreEntity("Some Store", "Some Location"));
    entityManager.clear();

    List<StoreEntity> results = repository.findByName("NonExistent");

    assertTrue(results.isEmpty());
  }

  @Test
  @DisplayName("Should find store by name and location")
  void shouldFindByNameAndLocation() {
    repository.saveAndFlush(new StoreEntity("Toko Maju", "Bandung"));
    entityManager.clear();

    Optional<StoreEntity> found = repository.findByNameAndLocation("Toko Maju", "Bandung");

    assertTrue(found.isPresent());
    assertEquals("Toko Maju", found.get().getName());
    assertEquals("Bandung", found.get().getLocation());
  }

  @Test
  @DisplayName("Should return empty when name and location not found")
  void shouldReturnNullWhenNameAndLocationNotFound() {
    repository.saveAndFlush(new StoreEntity("Existing", "Existing Loc"));
    entityManager.clear();

    Optional<StoreEntity> found =
        repository.findByNameAndLocation("NonExistent", "NonExistent Loc");

    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should check if store exists by name and location")
  void shouldCheckExistsByNameAndLocation() {
    repository.saveAndFlush(new StoreEntity("Toko ABC", "Surabaya"));
    entityManager.clear();

    assertTrue(repository.existsByNameAndLocation("Toko ABC", "Surabaya"));
    assertFalse(repository.existsByNameAndLocation("Toko ABC", "Jakarta"));
  }

  @Test
  @DisplayName("Should allow multiple stores with same name but different location")
  void shouldAllowMultipleStoresWithSameName() {
    var store1 = new StoreEntity("Same Name Store", "Location A");
    repository.saveAndFlush(store1);
    entityManager.clear();

    var store2 = new StoreEntity("Same Name Store", "Location B");
    repository.saveAndFlush(store2);
    entityManager.clear();

    List<StoreEntity> results = repository.findByName("Same Name Store");
    assertEquals(2, results.size());
  }

  @Test
  @DisplayName("Should persist all store entity fields")
  void shouldPersistAllStoreFields() {
    var entity = new StoreEntity("Lengkap Store", "Jakarta Pusat");
    entity.setChain("Lengkap Group");
    entity.setAddress("Jl. Sudirman No. 10");
    entity.setLatitude(-6.2146);
    entity.setLongitude(106.8451);
    entity.setStatus("ACTIVE");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId()).orElseThrow();

    assertEquals("Lengkap Store", found.getName());
    assertEquals("Jakarta Pusat", found.getLocation());
    assertEquals("Lengkap Group", found.getChain());
    assertEquals("Jl. Sudirman No. 10", found.getAddress());
    assertEquals(-6.2146, found.getLatitude(), 0.001);
    assertEquals(106.8451, found.getLongitude(), 0.001);
    assertEquals("ACTIVE", found.getStatus());
    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
  }
}
