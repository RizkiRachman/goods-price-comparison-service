package com.example.goodsprice.receipt.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.receipt.application.port.in.ReceiptApprovalInPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptApprovalService implements ReceiptApprovalInPort {

  private final ReceiptRepositoryPort receiptRepository;
  private final ReceiptEventOutPort eventOutPort;

  @Override
  @Transactional
  @ActivityLog
  public void approve(UUID id) {
    var receipt = receiptRepository.findById(id);
    if (Objects.isNull(receipt)) throw NotFoundException.receipt(id);

    receipt.markAsApproved();
    receiptRepository.save(receipt);
    eventOutPort.publishReceiptApproved(receipt);
    log.info("Receipt approved: {}", id);
  }
}
