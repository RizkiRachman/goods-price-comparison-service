package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptCorrectedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReceiptCorrectedEventHandler extends AbstractReceiptEventHandler {

  public ReceiptCorrectedEventHandler(
      ReceiptInPort receiptInPort, ReceiptEventHandlerHelper helper) {
    super(receiptInPort, helper);
  }

  @Override
  protected String getEventTypeLabel() {
    return "correction";
  }

  @Async("receiptApproveProcessorExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ReceiptCorrectedEvent event) {
    processReceiptEvent(event.receiptId());
  }
}
