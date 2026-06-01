package com.example.goodsprice.price.infrastructure.adapter.event;

import com.example.goodsprice.price.application.port.out.PriceSummaryEventOutPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceSummaryEventAdapter implements PriceSummaryEventOutPort {
  private final ApplicationEventPublisher publisher;

  @Override
  public void publishPriceSummaryUpdateRequested(UUID receiptId) {
    publisher.publishEvent(new PriceSummaryUpdateRequestedEvent(receiptId));
  }
}
