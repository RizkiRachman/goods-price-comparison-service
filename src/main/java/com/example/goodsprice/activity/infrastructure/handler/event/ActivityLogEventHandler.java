package com.example.goodsprice.activity.infrastructure.handler.event;

import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.infrastructure.adapter.event.ActivityLoggedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityLogEventHandler {

  private final ActivityLogInPort activityLogInPort;

  @Async("activityLogExecutor")
  @EventListener
  public void handle(ActivityLoggedEvent event) {
    log.debug(
        "[Async] Persisting activity log: {} {}",
        event.activity().getType(),
        event.activity().getDescription());
    activityLogInPort.log(event.activity());
  }
}
