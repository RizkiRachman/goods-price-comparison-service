package com.example.goodsprice.receipt.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.ReceiptsApi;
import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import com.example.goodsprice.api.model.ReceiptApproveResponse;
import com.example.goodsprice.api.model.ReceiptCorrectRequest;
import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptRejectResponse;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import com.example.goodsprice.common.web.ControllerResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ReceiptController implements ReceiptsApi {

  private final ReceiptWebAdapter adapter;
  private final ReceiptCorrectionWebAdapter correctionAdapter;

  @Override
  public ResponseEntity<ReceiptUploadResponse> uploadReceipt(MultipartFile image) {
    return ResponseEntity.accepted().body(adapter.upload(image));
  }

  @Override
  public ResponseEntity<ReceiptStatusResponse> getReceiptStatus(UUID id) {
    return ControllerResponse.ok(adapter.getStatus(id));
  }

  @Override
  public ResponseEntity<ReceiptResultResponse> getReceiptResults(UUID id) {
    return ControllerResponse.ok(adapter.getResult(id));
  }

  @Override
  public ResponseEntity<ReceiptApproveResponse> approveReceipt(UUID id) {
    return ControllerResponse.ok(adapter.approve(id));
  }

  @Override
  public ResponseEntity<ReceiptRejectResponse> rejectReceipt(UUID id) {
    return ControllerResponse.ok(adapter.reject(id));
  }

  @Override
  public ResponseEntity<ReceiptResultResponse> correctReceipt(
      UUID id, @Valid ReceiptCorrectRequest receiptCorrectRequest) {
    return ControllerResponse.ok(correctionAdapter.correct(id, receiptCorrectRequest));
  }

  @Override
  public ResponseEntity<ReceiptResultResponse> createReceipt(
      @Valid ReceiptCreateRequest receiptCreateRequest) {
    return ControllerResponse.ok(adapter.create(receiptCreateRequest));
  }

  @Override
  public ResponseEntity<BillSplitResponse> splitBill(
      UUID receiptId, @Valid BillSplitRequest request) {
    return ControllerResponse.ok(adapter.splitBill(receiptId, request));
  }
}
