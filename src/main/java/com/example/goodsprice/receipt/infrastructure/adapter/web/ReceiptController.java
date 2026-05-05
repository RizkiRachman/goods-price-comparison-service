package com.example.goodsprice.receipt.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.ReceiptsApi;
import com.example.goodsprice.api.model.ReceiptApproveResponse;
import com.example.goodsprice.api.model.ReceiptRejectRequest;
import com.example.goodsprice.api.model.ReceiptRejectResponse;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ReceiptController implements ReceiptsApi {

  private final ReceiptWebAdapter adapter;

  @Override
  public ResponseEntity<ReceiptUploadResponse> uploadReceipt(MultipartFile image) {
    var response = adapter.upload(image);
    return ResponseEntity.accepted().body(response);
  }

  @Override
  public ResponseEntity<ReceiptStatusResponse> getReceiptStatus(UUID id) {
    return ResponseEntity.ok(adapter.getStatus(id));
  }

  @Override
  public ResponseEntity<ReceiptResultResponse> getReceiptResults(UUID id) {
    return ResponseEntity.ok(adapter.getResult(id));
  }

  @Override
  public ResponseEntity<ReceiptApproveResponse> approveReceipt(UUID id) {
    return ResponseEntity.ok(adapter.approve(id));
  }

  @Override
  public ResponseEntity<ReceiptRejectResponse> rejectReceipt(
      UUID id, ReceiptRejectRequest receiptRejectRequest) {
    return ResponseEntity.ok(adapter.reject(id));
  }
}
