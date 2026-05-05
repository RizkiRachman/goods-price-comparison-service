package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptUploadedEventHandler {

  private final ReceiptInPort receiptInPort;

  @Async("receiptProcessorExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ReceiptUploadedEvent event) {
    log.info("[Async] Processing receipt: {}", event.receiptId());
    receiptInPort.process(event.receiptId(), event.imageBytes());
  }
}
