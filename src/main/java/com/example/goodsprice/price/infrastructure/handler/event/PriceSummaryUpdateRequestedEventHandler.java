package com.example.goodsprice.price.infrastructure.handler.event;

import com.example.goodsprice.price.application.domain.service.PriceSummaryBatchService;
import com.example.goodsprice.price.infrastructure.adapter.event.PriceSummaryUpdateRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceSummaryUpdateRequestedEventHandler {
  private final PriceSummaryBatchService batchService;

  @Async("receiptApproveProcessorExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(PriceSummaryUpdateRequestedEvent event) {
    log.info("[Async] Triggering price calculation after correction: {}", event.receiptId());
    try {
      batchService.updateSummaries();
      log.info("Price calculation completed after correction: {}", event.receiptId());
    } catch (Exception e) {
      log.error("Price calculation failed after correction: {}", event.receiptId(), e);
    }
  }
}
