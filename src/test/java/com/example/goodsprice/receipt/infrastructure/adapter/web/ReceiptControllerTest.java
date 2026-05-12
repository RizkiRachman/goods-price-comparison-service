package com.example.goodsprice.receipt.infrastructure.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerTest {

  @Mock private ReceiptWebAdapter adapter;
  @Mock private ReceiptCorrectionWebAdapter correctionAdapter;

  @InjectMocks private ReceiptController controller;

  @Test
  void shouldCreateReceiptAndReturnOkWithResultResponse() {
    var request = new ReceiptCreateRequest()
        .storeName("Toko Segar");
    var receiptId = UUID.randomUUID();
    var expectedResponse = new ReceiptResultResponse()
        .receiptId(receiptId)
        .storeName("Toko Segar");

    when(adapter.create(request)).thenReturn(expectedResponse);

    var response = controller.createReceipt(request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getReceiptId()).isEqualTo(receiptId);
    assertThat(response.getBody().getStoreName()).isEqualTo("Toko Segar");
  }

  @Test
  void shouldReturnOkWhenCreateReceiptWithMinimalRequest() {
    var request = new ReceiptCreateRequest();
    var responseBody = new ReceiptResultResponse();

    when(adapter.create(request)).thenReturn(responseBody);

    var response = controller.createReceipt(request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }
}
