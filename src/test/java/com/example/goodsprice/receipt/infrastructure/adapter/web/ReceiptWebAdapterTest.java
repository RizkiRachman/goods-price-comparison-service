package com.example.goodsprice.receipt.infrastructure.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptItem;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCreateDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.web.mapper.ReceiptDtoMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptWebAdapterTest {

  @Mock private ReceiptInPort receiptInPort;
  @Mock private ReceiptDtoMapper mapper;

  @InjectMocks private ReceiptWebAdapter adapter;

  @Test
  void shouldCreateReceiptAndReturnResultResponse() {
    var request = new ReceiptCreateRequest()
        .storeName("Toko Segar")
        .storeLocation("Jakarta")
        .date(LocalDate.of(2026, 5, 8))
        .totalAmount(10.0)
        .items(List.of(new ReceiptItem()
            .productName("Apple")
            .category("Fruit")
            .quantity(2.0)
            .unitPrice(5.0)
            .totalPrice(10.0)
            .unit("KG")));

    var createDomain = ReceiptCreateDomain.builder()
        .storeName("Toko Segar")
        .storeLocation("Jakarta")
        .receiptDate("2026-05-08")
        .totalAmount(new BigDecimal("10.00"))
        .items(List.of())
        .build();

    var receiptId = UUID.randomUUID();
    var resultReceipt = ReceiptDomain.builder()
        .id(receiptId)
        .storeName("Toko Segar")
        .storeLocation("Jakarta")
        .status(ReceiptStatus.APPROVED)
        .totalAmount(new BigDecimal("10.00"))
        .receiptDate("2026-05-08")
        .build();

    when(mapper.toCreateDomain(request)).thenReturn(createDomain);
    when(receiptInPort.create(createDomain)).thenReturn(resultReceipt);
    when(mapper.toResultResponse(resultReceipt)).thenAnswer(invocation -> {
      var r = new com.example.goodsprice.api.model.ReceiptResultResponse();
      r.setReceiptId(receiptId);
      r.setStoreName("Toko Segar");
      r.setTotalAmount(10.0);
      return r;
    });

    var response = adapter.create(request);

    assertThat(response).isNotNull();
    assertThat(response.getReceiptId()).isEqualTo(receiptId);
    assertThat(response.getStoreName()).isEqualTo("Toko Segar");
    assertThat(response.getTotalAmount()).isEqualTo(10.0);
    verify(mapper).toCreateDomain(request);
    verify(receiptInPort).create(createDomain);
    verify(mapper).toResultResponse(resultReceipt);
  }
}
