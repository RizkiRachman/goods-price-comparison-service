package com.example.goodsprice.receipt.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptApprovalServiceTest {

  @Mock private ReceiptRepositoryPort receiptRepository;
  @Mock private ReceiptEventOutPort eventOutPort;

  @InjectMocks private ReceiptApprovalService receiptApprovalService;

  @Test
  void shouldApproveReceiptSuccessfully() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).status(ReceiptStatus.PENDING).build();
    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);

    receiptApprovalService.approve(id);

    verify(receiptRepository).findById(id);
    verify(receiptRepository).save(any(ReceiptDomain.class));
    verify(eventOutPort).publishReceiptApproved(receipt);
  }

  @Test
  void shouldThrowNotFoundWhenReceiptDoesNotExist() {
    var id = UUID.randomUUID();
    when(receiptRepository.findById(id)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> receiptApprovalService.approve(id));
  }
}
