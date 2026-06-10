package com.example.goodsprice.price.infrastructure.handler.event;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
public abstract class AbstractAsyncPriceCalcHandler<T> {

  @Async("receiptApproveProcessorExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleEvent(T event) {
    log.info("[Async] Triggering price calculation after: {}", event);
    try {
      doExecute(event);
      log.info("Price calculation completed successfully: {}", event);
    } catch (Exception e) {
      log.error("Price calculation failed: {}", Objects.toString(event), e);
    }
  }

  protected abstract void doExecute(T event);
}
