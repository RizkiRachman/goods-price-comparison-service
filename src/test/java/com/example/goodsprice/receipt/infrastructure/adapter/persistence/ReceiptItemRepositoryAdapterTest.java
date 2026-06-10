package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.receipt.application.domain.model.ReceiptItemDomain;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptItemEntity;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptItemRepositoryAdapterTest {

  @Mock private JpaReceiptItemRepository jpaRepo;

  @InjectMocks private ReceiptItemRepositoryAdapter adapter;

  @Test
  void shouldSaveAllItems() {
    var items =
        List.of(
            ReceiptItemDomain.builder()
                .receiptId(UUID.randomUUID())
                .productName("Apple")
                .quantity(2.0)
                .unitPrice(5.0)
                .totalPrice(10.0)
                .build());

    adapter.saveAll(items);

    verify(jpaRepo).saveAll(any());
  }

  @Test
  void shouldFindByReceiptId() {
    var receiptId = UUID.randomUUID();
    var entity = new ReceiptItemEntity();
    entity.setId(1L);
    entity.setReceiptId(receiptId);
    entity.setProductName("Apple");
    entity.setQuantity(2.0);
    entity.setUnitPrice(5.0);
    entity.setTotalPrice(10.0);
    entity.setUnit("KG");

    when(jpaRepo.findByReceiptId(receiptId)).thenReturn(List.of(entity));

    var result = adapter.findByReceiptId(receiptId);

    assertNotNull(result);
    assertEquals(1, result.size());
    var item = result.get(0);
    assertEquals(receiptId, item.getReceiptId());
    assertEquals("Apple", item.getProductName());
    assertEquals(2.0, item.getQuantity());
    assertEquals(5.0, item.getUnitPrice());
    assertEquals(10.0, item.getTotalPrice());
    assertEquals("KG", item.getUnit());
  }

  @Test
  void shouldReturnEmptyListWhenNoItemsFound() {
    var receiptId = UUID.randomUUID();
    when(jpaRepo.findByReceiptId(receiptId)).thenReturn(List.of());

    var result = adapter.findByReceiptId(receiptId);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void shouldSaveAllWithMultipleItems() {
    var receiptId = UUID.randomUUID();
    var items =
        List.of(
            ReceiptItemDomain.builder().receiptId(receiptId).productName("Apple").build(),
            ReceiptItemDomain.builder().receiptId(receiptId).productName("Milk").build());

    adapter.saveAll(items);

    verify(jpaRepo).saveAll(any());
  }

  @Test
  void shouldMapAllFieldsFromEntityToDomain() {
    var receiptId = UUID.randomUUID();
    var entity = new ReceiptItemEntity();
    entity.setId(1L);
    entity.setReceiptId(receiptId);
    entity.setProductName("Milk");
    entity.setCategory("Dairy");
    entity.setQuantity(1.0);
    entity.setUnitPrice(15.0);
    entity.setTotalPrice(15.0);
    entity.setUnit("LITER");

    when(jpaRepo.findByReceiptId(receiptId)).thenReturn(List.of(entity));

    var result = adapter.findByReceiptId(receiptId);

    assertEquals(1, result.size());
    var item = result.get(0);
    assertEquals(1L, item.getId());
    assertEquals("Milk", item.getProductName());
    assertEquals("Dairy", item.getCategory());
    assertEquals("LITER", item.getUnit());
  }
}
