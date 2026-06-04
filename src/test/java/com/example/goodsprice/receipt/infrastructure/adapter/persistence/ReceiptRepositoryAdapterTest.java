package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptRepositoryAdapterTest {

  @Mock private JpaReceiptRepository jpaRepo;
  @Mock private ReceiptMapper mapper;

  @InjectMocks private ReceiptRepositoryAdapter adapter;

  @Test
  void shouldSaveReceipt() {
    var domain = ReceiptDomain.builder().imageHash("hash123").build();
    var entity = new ReceiptEntity();
    var savedEntity = new ReceiptEntity();
    var savedDomain = ReceiptDomain.builder().imageHash("hash123").build();

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(jpaRepo.save(entity)).thenReturn(savedEntity);
    when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

    var result = adapter.save(domain);

    assertNotNull(result);
    assertEquals("hash123", result.getImageHash());
    verify(jpaRepo).save(entity);
  }

  @Test
  void shouldFindByIdWhenExists() {
    var id = UUID.randomUUID();
    var entity = new ReceiptEntity();
    var domain = ReceiptDomain.builder().id(id).build();

    when(jpaRepo.findById(id)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
  }

  @Test
  void shouldReturnNullWhenFindByIdNotFound() {
    var id = UUID.randomUUID();
    when(jpaRepo.findById(id)).thenReturn(Optional.empty());

    assertNull(adapter.findById(id));
  }

  @Test
  void shouldFindByImageHashWhenExists() {
    var hash = "hash123";
    var entity = new ReceiptEntity();
    var domain = ReceiptDomain.builder().imageHash(hash).build();

    when(jpaRepo.findByImageHash(hash)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    var result = adapter.findByImageHash(hash);

    assertNotNull(result);
    assertEquals(hash, result.getImageHash());
  }

  @Test
  void shouldReturnNullWhenFindByImageHashNotFound() {
    var hash = "nonexistent";
    when(jpaRepo.findByImageHash(hash)).thenReturn(Optional.empty());

    assertNull(adapter.findByImageHash(hash));
  }

  @Test
  void shouldCheckExistsByImageHash() {
    when(jpaRepo.existsByImageHash("hash123")).thenReturn(true);

    var result = adapter.existsByImageHash("hash123");

    assertEquals(true, result);
    verify(jpaRepo).existsByImageHash("hash123");
  }

  @Test
  void shouldCheckExistsById() {
    var id = UUID.randomUUID();
    when(jpaRepo.existsById(id)).thenReturn(true);

    var result = adapter.existsById(id);

    assertEquals(true, result);
    verify(jpaRepo).existsById(id);
  }

  @Test
  void shouldDeleteById() {
    var id = UUID.randomUUID();

    adapter.deleteById(id);

    verify(jpaRepo).deleteById(id);
  }

  @Test
  void shouldUpdateImageData() {
    var id = UUID.randomUUID();
    var data = new byte[] {1, 2, 3};

    adapter.updateImageData(id, data);

    verify(jpaRepo).updateImageData(id, data);
  }

  @Test
  void shouldReturnNullWhenFindStatusOnlyNotFound() {
    var id = UUID.randomUUID();
    when(jpaRepo.findById(id)).thenReturn(Optional.empty());

    assertNull(adapter.findById(id));
    verify(jpaRepo).findById(id);
  }

  @Test
  void shouldReturnFalseWhenExistsByImageHashNotFound() {
    when(jpaRepo.existsByImageHash("missing")).thenReturn(false);

    assertEquals(false, adapter.existsByImageHash("missing"));
  }

  @Test
  void shouldReturnFalseWhenExistsByIdNotFound() {
    var id = UUID.randomUUID();
    when(jpaRepo.existsById(id)).thenReturn(false);

    assertEquals(false, adapter.existsById(id));
  }
}
