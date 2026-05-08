package com.example.goodsprice.receipt.infrastructure.adapter.web;

import com.example.goodsprice.api.model.ReceiptApproveResponse;
import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptRejectResponse;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.ReceiptStatusResponse;
import com.example.goodsprice.api.model.ReceiptUploadResponse;
import com.example.goodsprice.api.model.Status;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.web.mapper.ReceiptDtoMapper;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptWebAdapter {

  private final ReceiptInPort receiptInPort;
  private final ReceiptDtoMapper mapper;

  public ReceiptUploadResponse upload(MultipartFile image) {
    try {
      var receipt = receiptInPort.upload(image.getBytes(), image.getOriginalFilename());
      var response = new ReceiptUploadResponse();
      response.setReceiptId(receipt.getId());
      response.setStatus(mapper.toStatus(receipt.getStatus()));
      return response;
    } catch (IOException e) {
      throw new RuntimeException("Failed to read image bytes", e);
    }
  }

  public ReceiptStatusResponse getStatus(UUID id) {
    var status = receiptInPort.getStatus(id);
    var response = new ReceiptStatusResponse();
    response.setReceiptId(id);
    response.setStatus(ObjectUtils.getOrDefault(status, mapper::toStatus, Status.PENDING));
    return response;
  }

  public ReceiptResultResponse getResult(UUID id) {
    return mapper.toResultResponse(receiptInPort.findById(id));
  }

  public ReceiptApproveResponse approve(UUID id) {
    receiptInPort.approve(id);
    return new ReceiptApproveResponse()
        .receiptId(id)
        .status(mapper.toStatus(receiptInPort.findById(id).getStatus()));
  }

  public ReceiptRejectResponse reject(UUID id) {
    receiptInPort.reject(id);
    return new ReceiptRejectResponse()
        .receiptId(id)
        .status(mapper.toStatus(receiptInPort.findById(id).getStatus()));
  }

  public void create(ReceiptCreateRequest request) {
    var domain = mapper.toCreateDomain(request);
    receiptInPort.create(domain);
  }
}
