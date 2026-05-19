package com.example.goodsprice.activity.infrastructure.handler.event;

import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.infrastructure.adapter.event.ActivityLoggedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityLogEventHandler {

  private final ActivityLogInPort activityLogInPort;

  @Async("activityLogExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ActivityLoggedEvent event) {
    log.debug(
        "[Async] Persisting activity log: {} {}",
        event.activity().getType(),
        event.activity().getDescription());
    activityLogInPort.log(event.activity());
  }
}
