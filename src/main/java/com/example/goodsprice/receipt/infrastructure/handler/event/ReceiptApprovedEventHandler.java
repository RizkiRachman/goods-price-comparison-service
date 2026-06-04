package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptApprovedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReceiptApprovedEventHandler extends AbstractReceiptEventHandler {

  public ReceiptApprovedEventHandler(
      ReceiptInPort receiptInPort, ReceiptEventHandlerHelper helper) {
    super(receiptInPort, helper);
  }

  @Override
  protected String getEventTypeLabel() {
    return "approval";
  }

  @Async("receiptApproveProcessorExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ReceiptApprovedEvent event) {
    processReceiptEvent(event.receiptId());
  }
}
