package com.example.goodsprice.receipt.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCorrectionDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptItemCorrectionDomain;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReceiptCorrectionServiceTest {

  @Mock private ReceiptRepositoryPort receiptRepository;
  @Mock private ReceiptEventOutPort eventOutPort;
  @Mock private ObjectMapper objectMapper;
  @InjectMocks private ReceiptCorrectionService service;

  @Test
  void shouldCorrectReceiptFields() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).build();
    var correction =
        ReceiptCorrectionDomain.builder()
            .storeName("New Store")
            .storeLocation("New Location")
            .receiptDate("2026-01-01")
            .totalAmount(new BigDecimal("200.00"))
            .build();

    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);

    var result = service.correct(id, correction);

    assertNotNull(result);
    verify(receiptRepository).save(any(ReceiptDomain.class));
    verify(eventOutPort).publishReceiptCorrected(any());
  }

  @Test
  void shouldThrowWhenReceiptNotFound() {
    var id = UUID.randomUUID();
    when(receiptRepository.findById(id)).thenReturn(null);

    assertThrows(
        NotFoundException.class,
        () -> service.correct(id, ReceiptCorrectionDomain.builder().storeName("X").build()));
  }

  @Test
  void shouldCorrectReceiptWithNullItems() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).storeName("Old Store").build();
    var correction = ReceiptCorrectionDomain.builder().storeName("New Store").items(null).build();

    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);

    var result = service.correct(id, correction);

    assertNotNull(result);
    assertEquals("New Store", result.getStoreName());
    verify(eventOutPort).publishReceiptCorrected(any());
  }

  @SuppressWarnings("unchecked")
  @Test
  void shouldCorrectReceiptWithItems() throws Exception {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).build();
    var item =
        ReceiptItemCorrectionDomain.builder()
            .productName("Apple")
            .category("Fruit")
            .quantity(2.0)
            .unitPrice(5.0)
            .totalPrice(10.0)
            .unit("KG")
            .build();
    var correction =
        ReceiptCorrectionDomain.builder().storeName("Store").items(List.of(item)).build();

    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);
    when(objectMapper.writeValueAsString(any())).thenReturn("{\"items\":[]}");

    var result = service.correct(id, correction);

    assertNotNull(result);
    verify(objectMapper).writeValueAsString(any());
    verify(eventOutPort).publishReceiptCorrected(any());
  }

  @Test
  void shouldThrowWhenObjectMapperFailsForItems() throws Exception {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).build();
    var item = ReceiptItemCorrectionDomain.builder().productName("Apple").build();
    var correction =
        ReceiptCorrectionDomain.builder().storeName("Store").items(List.of(item)).build();

    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(objectMapper.writeValueAsString(any()))
        .thenThrow(new JsonProcessingException("serialization error") {});

    assertThrows(RuntimeException.class, () -> service.correct(id, correction));
  }
}
