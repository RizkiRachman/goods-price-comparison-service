package com.example.goodsprice.receipt.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptResultResponse;
import com.example.goodsprice.api.model.Status;
import com.example.goodsprice.receipt.application.domain.model.BillSplitRequestDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitResponseDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitType;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCreateDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.application.port.in.BillSplitInPort;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.web.mapper.BillSplitDtoMapper;
import com.example.goodsprice.receipt.infrastructure.adapter.web.mapper.ReceiptDtoMapper;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ReceiptWebAdapterTest {

  @Mock private ReceiptInPort receiptInPort;
  @Mock private BillSplitInPort billSplitInPort;
  @Mock private BillSplitDtoMapper billSplitDtoMapper;
  @Mock private ReceiptDtoMapper mapper;
  @Mock private MultipartFile multipartFile;

  @InjectMocks private ReceiptWebAdapter adapter;

  private final UUID receiptId = UUID.randomUUID();

  @BeforeEach
  void setUp() {}

  @Test
  void shouldUpload() throws Exception {
    var receipt = ReceiptDomain.builder().id(receiptId).status(ReceiptStatus.PENDING).build();
    when(multipartFile.getBytes()).thenReturn("image-data".getBytes());
    when(multipartFile.getOriginalFilename()).thenReturn("receipt.jpg");
    when(receiptInPort.upload(any(), any())).thenReturn(receipt);
    when(mapper.toStatus(ReceiptStatus.PENDING)).thenReturn(Status.PENDING);

    var result = adapter.upload(multipartFile);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
    assertEquals(Status.PENDING, result.getStatus());
  }

  @Test
  void shouldThrowWhenUploadFails() throws Exception {
    when(multipartFile.getBytes()).thenThrow(new IOException("IO error"));

    assertThrows(RuntimeException.class, () -> adapter.upload(multipartFile));
  }

  @Test
  void shouldGetStatus() {
    when(receiptInPort.getStatus(receiptId)).thenReturn(ReceiptStatus.APPROVED);
    when(mapper.toStatus(ReceiptStatus.APPROVED)).thenReturn(Status.APPROVED);

    var result = adapter.getStatus(receiptId);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
    assertEquals(Status.APPROVED, result.getStatus());
  }

  @Test
  void shouldGetStatusWithNullStatus() {
    when(receiptInPort.getStatus(receiptId)).thenReturn(null);

    var result = adapter.getStatus(receiptId);

    assertNotNull(result);
    assertEquals(Status.PENDING, result.getStatus());
  }

  @Test
  void shouldGetResult() {
    var receipt = ReceiptDomain.builder().id(receiptId).storeName("Toko Segar").build();
    var response = new ReceiptResultResponse().receiptId(receiptId).storeName("Toko Segar");
    when(receiptInPort.findById(receiptId)).thenReturn(receipt);
    when(mapper.toResultResponse(receipt)).thenReturn(response);

    var result = adapter.getResult(receiptId);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
    assertEquals("Toko Segar", result.getStoreName());
  }

  @Test
  void shouldApprove() {
    var receipt = ReceiptDomain.builder().id(receiptId).status(ReceiptStatus.APPROVED).build();
    when(receiptInPort.findById(receiptId)).thenReturn(receipt);
    when(mapper.toStatus(ReceiptStatus.APPROVED)).thenReturn(Status.APPROVED);

    var result = adapter.approve(receiptId);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
    assertEquals(Status.APPROVED, result.getStatus());
    verify(receiptInPort).approve(receiptId);
  }

  @Test
  void shouldReject() {
    var receipt = ReceiptDomain.builder().id(receiptId).status(ReceiptStatus.REJECTED).build();
    when(receiptInPort.findById(receiptId)).thenReturn(receipt);
    when(mapper.toStatus(ReceiptStatus.REJECTED)).thenReturn(Status.REJECTED);

    var result = adapter.reject(receiptId);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
    assertEquals(Status.REJECTED, result.getStatus());
    verify(receiptInPort).reject(receiptId);
  }

  @Test
  void shouldCreate() {
    var request = new ReceiptCreateRequest().storeName("Toko Segar");
    var createDomain = ReceiptCreateDomain.builder().storeName("Toko Segar").build();
    var receipt = ReceiptDomain.builder().id(receiptId).storeName("Toko Segar").build();
    var response = new ReceiptResultResponse().receiptId(receiptId);

    when(mapper.toCreateDomain(request)).thenReturn(createDomain);
    when(receiptInPort.create(createDomain)).thenReturn(receipt);
    when(mapper.toResultResponse(receipt)).thenReturn(response);

    var result = adapter.create(request);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
  }

  @Test
  void shouldSplitBill() {
    var request = new BillSplitRequest();
    var domainRequest = BillSplitRequestDomain.builder().type(BillSplitType.RATIO).build();
    var domainResponse =
        BillSplitResponseDomain.builder().receiptId(receiptId).type(BillSplitType.RATIO).build();
    var response = new BillSplitResponse().receiptId(receiptId);

    when(billSplitDtoMapper.toRequestDomain(request)).thenReturn(domainRequest);
    when(billSplitInPort.splitBill(receiptId, domainRequest)).thenReturn(domainResponse);
    when(billSplitDtoMapper.toResponseDto(domainResponse)).thenReturn(response);

    var result = adapter.splitBill(receiptId, request);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
  }
}
