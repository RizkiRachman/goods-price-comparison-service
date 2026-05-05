package com.example.goodsprice.receipt.infrastructure.adapter.event;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReceiptEventAdapter implements ReceiptEventOutPort {

  private final ApplicationEventPublisher publisher;

  @Override
  public void publishReceiptUploaded(ReceiptDomain receipt, byte[] imageBytes) {
    publisher.publishEvent(
        new ReceiptUploadedEvent(
            receipt.getId(), receipt.getImageHash(), imageBytes, receipt.getOriginalFilename()));
  }

  @Override
  public void publishReceiptProcessed(ReceiptDomain receipt) {
    publisher.publishEvent(new ReceiptProcessedEvent(receipt.getId()));
  }

  @Override
  public void publishReceiptApproved(ReceiptDomain receipt) {
    publisher.publishEvent(new ReceiptApprovedEvent(receipt.getId()));
  }

  @Override
  public void publishReceiptCorrected(ReceiptDomain receipt) {
    publisher.publishEvent(new ReceiptCorrectedEvent(receipt.getId()));
  }
}
