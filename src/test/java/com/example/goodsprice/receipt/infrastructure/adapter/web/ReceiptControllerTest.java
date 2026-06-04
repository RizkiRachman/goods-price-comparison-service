package com.example.goodsprice.receipt.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import com.example.goodsprice.api.model.ReceiptApproveResponse;
import com.example.goodsprice.api.model.ReceiptCorrectRequest;
import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptRejectResponse;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerTest {

  @Mock private ReceiptWebAdapter adapter;
  @Mock private ReceiptCorrectionWebAdapter correctionAdapter;
  @Mock private MultipartFile multipartFile;

  @InjectMocks private ReceiptController controller;

  private final UUID receiptId = UUID.randomUUID();

  @Test
  void shouldUploadReceipt() {
    var response = new ReceiptUploadResponse().receiptId(receiptId);
    when(adapter.upload(any())).thenReturn(response);

    var result = controller.uploadReceipt(multipartFile);

    assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
    assertNotNull(result.getBody());
    assertEquals(receiptId, result.getBody().getReceiptId());
  }

  @Test
  void shouldGetReceiptStatus() {
    var response = new ReceiptStatusResponse().receiptId(receiptId);
    when(adapter.getStatus(receiptId)).thenReturn(response);

    var result = controller.getReceiptStatus(receiptId);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(receiptId, result.getBody().getReceiptId());
  }

  @Test
  void shouldGetReceiptResults() {
    var response = new ReceiptResultResponse().receiptId(receiptId);
    when(adapter.getResult(receiptId)).thenReturn(response);

    var result = controller.getReceiptResults(receiptId);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(receiptId, result.getBody().getReceiptId());
  }

  @Test
  void shouldApproveReceipt() {
    var response = new ReceiptApproveResponse().receiptId(receiptId);
    when(adapter.approve(receiptId)).thenReturn(response);

    var result = controller.approveReceipt(receiptId);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(receiptId, result.getBody().getReceiptId());
  }

  @Test
  void shouldRejectReceipt() {
    var response = new ReceiptRejectResponse().receiptId(receiptId);
    when(adapter.reject(receiptId)).thenReturn(response);

    var result = controller.rejectReceipt(receiptId);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(receiptId, result.getBody().getReceiptId());
  }

  @Test
  void shouldCorrectReceipt() {
    var request = new ReceiptCorrectRequest();
    var response = new ReceiptResultResponse().receiptId(receiptId);
    when(correctionAdapter.correct(receiptId, request)).thenReturn(response);

    var result = controller.correctReceipt(receiptId, request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(receiptId, result.getBody().getReceiptId());
  }

  @Test
  void shouldCreateReceipt() {
    var request = new ReceiptCreateRequest();
    var response = new ReceiptResultResponse().receiptId(receiptId);
    when(adapter.create(request)).thenReturn(response);

    var result = controller.createReceipt(request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(receiptId, result.getBody().getReceiptId());
  }

  @Test
  void shouldSplitBill() {
    var request = new BillSplitRequest();
    var response = new BillSplitResponse().receiptId(receiptId);
    when(adapter.splitBill(receiptId, request)).thenReturn(response);

    var result = controller.splitBill(receiptId, request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(receiptId, result.getBody().getReceiptId());
  }
}
