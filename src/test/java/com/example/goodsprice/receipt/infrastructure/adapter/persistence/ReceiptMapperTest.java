package com.example.goodsprice.receipt.infrastructure.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.infrastructure.adapter.persistence.entity.ReceiptEntity;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReceiptMapperTest {

  private final ReceiptMapper mapper = new ReceiptMapper();

  @Test
  void shouldMapDomainToEntity() {
    var id = UUID.randomUUID();
    var domain =
        ReceiptDomain.builder()
            .id(id)
            .imageHash("hash123")
            .originalFilename("receipt.jpg")
            .status(ReceiptStatus.COMPLETED)
            .storeName("Toko Segar")
            .storeLocation("Jakarta")
            .receiptDate("2026-05-08")
            .totalAmount(new BigDecimal("10.00"))
            .extractedDataJson("{\"storeName\":\"Toko Segar\"}")
            .errorMessage(null)
            .imageData(new byte[] {1, 2, 3})
            .build();

    var entity = mapper.toEntity(domain);

    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals("hash123", entity.getImageHash());
    assertEquals("receipt.jpg", entity.getOriginalFilename());
    assertEquals(ReceiptStatus.COMPLETED, entity.getStatus());
    assertEquals("Toko Segar", entity.getStoreName());
    assertEquals("Jakarta", entity.getStoreLocation());
    assertEquals("2026-05-08", entity.getReceiptDate());
    assertEquals(10.0, entity.getTotalAmount());
    assertEquals("{\"storeName\":\"Toko Segar\"}", entity.getExtractedDataJson());
  }

  @Test
  void shouldMapEntityToDomain() {
    var id = UUID.randomUUID();
    var entity = new ReceiptEntity();
    entity.setId(id);
    entity.setImageHash("hash123");
    entity.setOriginalFilename("receipt.jpg");
    entity.setStatus(ReceiptStatus.COMPLETED);
    entity.setStoreName("Toko Segar");
    entity.setStoreLocation("Jakarta");
    entity.setReceiptDate("2026-05-08");
    entity.setTotalAmount(10.0);
    entity.setExtractedDataJson("{\"storeName\":\"Toko Segar\"}");
    entity.setErrorMessage(null);
    entity.setImageData(new byte[] {1, 2, 3});

    var domain = mapper.toDomain(entity);

    assertNotNull(domain);
    assertEquals(id, domain.getId());
    assertEquals("hash123", domain.getImageHash());
    assertEquals("receipt.jpg", domain.getOriginalFilename());
    assertEquals(ReceiptStatus.COMPLETED, domain.getStatus());
    assertEquals("Toko Segar", domain.getStoreName());
    assertEquals("Jakarta", domain.getStoreLocation());
    assertEquals("2026-05-08", domain.getReceiptDate());
    assertEquals(0, new BigDecimal("10.00").compareTo(domain.getTotalAmount()));
    assertEquals("{\"storeName\":\"Toko Segar\"}", domain.getExtractedDataJson());
  }

  @Test
  void shouldReturnNullWhenMappingNullDomainToEntity() {
    assertNull(mapper.toEntity((ReceiptDomain) null));
  }

  @Test
  void shouldReturnNullWhenMappingNullEntityToDomain() {
    assertNull(mapper.toDomain(null));
  }

  @Test
  void shouldMapNullTotalAmount() {
    var entity = new ReceiptEntity();
    entity.setTotalAmount(null);

    var domain = mapper.toDomain(entity);

    assertNull(domain.getTotalAmount());
  }

  @Test
  void shouldMapNullTotalAmountFromDomain() {
    var domain = ReceiptDomain.builder().build();

    var entity = mapper.toEntity(domain);

    assertNull(entity.getTotalAmount());
  }
}
