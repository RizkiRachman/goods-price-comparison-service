package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptProcessedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReceiptProcessedEventHandler extends AbstractReceiptEventHandler {

  public ReceiptProcessedEventHandler(
      ReceiptInPort receiptInPort, ReceiptEventHandlerHelper helper) {
    super(receiptInPort, helper);
  }

  @Override
  protected String getEventTypeLabel() {
    return "processing";
  }

  @Async("receiptProcessorExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ReceiptProcessedEvent event) {
    processReceiptEvent(event.receiptId());
  }
}
