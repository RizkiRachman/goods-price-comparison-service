package com.example.goodsprice.receipt.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.receipt.application.domain.model.ReceiptCorrectionDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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
        com.example.goodsprice.common.exception.NotFoundException.class,
        () -> service.correct(id, ReceiptCorrectionDomain.builder().storeName("X").build()));
  }
}
