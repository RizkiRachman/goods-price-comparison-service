package com.example.goodsprice.receipt.application.domain.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.service.ServiceLayerNotFoundExceptionTest;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptApprovalServiceTest implements ServiceLayerNotFoundExceptionTest {

  @Mock private ReceiptRepositoryPort receiptRepository;
  @Mock private ReceiptEventOutPort eventOutPort;

  @InjectMocks private ReceiptApprovalService receiptApprovalService;

  private final UUID testId = UUID.randomUUID();

  @Override
  public void mockRepositoryReturnsNull() {
    when(receiptRepository.findById(testId)).thenReturn(null);
  }

  @Override
  public Executable serviceMethodThatShouldThrowNotFound() {
    return () -> receiptApprovalService.approve(testId);
  }

  @Test
  void shouldApproveReceiptSuccessfully() {
    var id = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(id).status(ReceiptStatus.PENDING).build();
    when(receiptRepository.findById(id)).thenReturn(receipt);
    when(receiptRepository.save(any(ReceiptDomain.class))).thenReturn(receipt);

    receiptApprovalService.approve(id);

    verify(receiptRepository).save(any(ReceiptDomain.class));
    verify(eventOutPort).publishReceiptApproved(receipt);
  }
}
