package com.example.goodsprice.activity.infrastructure.adapter.event;

import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.port.out.ActivityLogEventOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityLogEventAdapter implements ActivityLogEventOutPort {

  private final ApplicationEventPublisher publisher;

  @Override
  public void publishLogged(ActivityLogDomain activity) {
    publisher.publishEvent(new ActivityLoggedEvent(activity));
  }
}
