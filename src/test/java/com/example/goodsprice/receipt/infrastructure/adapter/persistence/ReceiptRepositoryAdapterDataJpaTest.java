package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReceiptRepositoryAdapterDataJpaTest {

  @Autowired private JpaReceiptRepository repository;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @DisplayName("Should persist and retrieve receipt with all fields")
  void shouldPersistAndRetrieveReceipt() {
    var entity = new ReceiptEntity("hash001", "receipt1.jpg");
    entity.setStoreName("Toko Makmur");
    entity.setStoreLocation("Jakarta");
    entity.setReceiptDate("2026-01-15");
    entity.setTotalAmount(50000.0);
    entity.setImageData(new byte[] {1, 2, 3});
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findById(entity.getId());

    assertTrue(found.isPresent());
    var receipt = found.get();
    assertEquals("hash001", receipt.getImageHash());
    assertEquals("receipt1.jpg", receipt.getOriginalFilename());
    assertEquals(ReceiptStatus.PENDING, receipt.getStatus());
    assertEquals("Toko Makmur", receipt.getStoreName());
    assertEquals("Jakarta", receipt.getStoreLocation());
    assertEquals("2026-01-15", receipt.getReceiptDate());
    assertEquals(50000.0, receipt.getTotalAmount(), 0.001);
    assertArrayEquals(new byte[] {1, 2, 3}, receipt.getImageData());
  }

  @Test
  @DisplayName("Should enforce unique image hash constraint")
  void shouldEnforceUniqueImageHash() {
    repository.saveAndFlush(new ReceiptEntity("unique-hash", "first.jpg"));
    entityManager.clear();

    var duplicate = new ReceiptEntity("unique-hash", "second.jpg");
    org.junit.jupiter.api.Assertions.assertThrows(
        DataIntegrityViolationException.class,
        () -> {
          repository.saveAndFlush(duplicate);
          entityManager.flush();
        });
  }

  @Test
  @DisplayName("Should return empty for non-existent id")
  void shouldReturnEmptyForNonExistentId() {
    Optional<ReceiptEntity> found = repository.findById(UUID.randomUUID());
    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should find receipt by image hash")
  void shouldFindByImageHash() {
    var entity = new ReceiptEntity("hash-find", "find.jpg");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findByImageHash("hash-find");
    assertTrue(found.isPresent());
    assertEquals("find.jpg", found.get().getOriginalFilename());
  }

  @Test
  @DisplayName("Should return empty when image hash not found")
  void shouldReturnEmptyWhenImageHashNotFound() {
    var found = repository.findByImageHash("nonexistent-hash");
    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("Should check if receipt exists by image hash")
  void shouldCheckExistsByImageHash() {
    repository.saveAndFlush(new ReceiptEntity("exists-hash", "exists.jpg"));
    entityManager.clear();

    assertTrue(repository.existsByImageHash("exists-hash"));
    assertFalse(repository.existsByImageHash("other-hash"));
  }

  @Test
  @DisplayName("Should update image data")
  void shouldUpdateImageData() {
    var entity = new ReceiptEntity("update-img", "update.jpg");
    repository.saveAndFlush(entity);
    entityManager.clear();

    byte[] newImageData = {4, 5, 6};
    repository.updateImageData(entity.getId(), newImageData);
    entityManager.flush();
    entityManager.clear();

    var found = repository.findById(entity.getId()).orElseThrow();
    assertArrayEquals(new byte[] {4, 5, 6}, found.getImageData());
  }

  @Test
  @DisplayName("Should persist entity status values")
  void shouldPersistEntityStatus() {
    var pending = new ReceiptEntity("hash-pending", "pending.jpg");
    pending.setStatus(ReceiptStatus.PENDING);
    repository.saveAndFlush(pending);

    var approved = new ReceiptEntity("hash-approved", "approved.jpg");
    approved.setStatus(ReceiptStatus.APPROVED);
    repository.saveAndFlush(approved);

    var rejected = new ReceiptEntity("hash-rejected", "rejected.jpg");
    rejected.setStatus(ReceiptStatus.REJECTED);
    repository.saveAndFlush(rejected);
    entityManager.clear();

    assertEquals(
        ReceiptStatus.PENDING,
        repository.findByImageHash("hash-pending").orElseThrow().getStatus());
    assertEquals(
        ReceiptStatus.APPROVED,
        repository.findByImageHash("hash-approved").orElseThrow().getStatus());
    assertEquals(
        ReceiptStatus.REJECTED,
        repository.findByImageHash("hash-rejected").orElseThrow().getStatus());
  }

  @Test
  @DisplayName("Should persist timestamps")
  void shouldPersistTimestamps() {
    var entity = new ReceiptEntity("hash-ts", "ts.jpg");
    repository.saveAndFlush(entity);
    entityManager.clear();

    var found = repository.findByImageHash("hash-ts").orElseThrow();

    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
  }
}
