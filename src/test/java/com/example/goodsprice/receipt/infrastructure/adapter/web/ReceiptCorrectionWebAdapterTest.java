package com.example.goodsprice.receipt.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.ReceiptCorrectRequest;
import com.example.goodsprice.api.model.ReceiptItem;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCorrectionDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.in.ReceiptCorrectionInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.web.mapper.ReceiptDtoMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptCorrectionWebAdapterTest {

  @Mock private ReceiptCorrectionInPort correctionInPort;
  @Mock private ReceiptDtoMapper mapper;

  @InjectMocks private ReceiptCorrectionWebAdapter adapter;

  @Test
  void shouldCorrectReceipt() {
    var receiptId = UUID.randomUUID();
    var request =
        new ReceiptCorrectRequest()
            .storeName("New Store")
            .storeLocation("New Location")
            .date(LocalDate.of(2026, 1, 1))
            .totalAmount(200.0)
            .items(
                List.of(
                    new ReceiptItem()
                        .productName("Apple")
                        .category("Fruit")
                        .quantity(2.0)
                        .unitPrice(5.0)
                        .totalPrice(10.0)
                        .unit("KG")));

    var receipt = ReceiptDomain.builder().id(receiptId).storeName("New Store").build();
    var response = new ReceiptResultResponse().receiptId(receiptId);

    when(correctionInPort.correct(any(), any(ReceiptCorrectionDomain.class))).thenReturn(receipt);
    when(mapper.toResultResponse(receipt)).thenReturn(response);

    var result = adapter.correct(receiptId, request);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
    verify(correctionInPort).correct(any(), any(ReceiptCorrectionDomain.class));
  }

  @Test
  void shouldCorrectReceiptWithNullItems() {
    var receiptId = UUID.randomUUID();
    var request =
        new ReceiptCorrectRequest()
            .storeName("New Store")
            .storeLocation(null)
            .date(null)
            .totalAmount(null)
            .items(null);

    var receipt = ReceiptDomain.builder().id(receiptId).storeName("New Store").build();
    var response = new ReceiptResultResponse().receiptId(receiptId);

    when(correctionInPort.correct(any(), any(ReceiptCorrectionDomain.class))).thenReturn(receipt);
    when(mapper.toResultResponse(receipt)).thenReturn(response);

    var result = adapter.correct(receiptId, request);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
  }
}
