package com.example.goodsprice.price.infrastructure.adapter.event;

import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class PriceSummaryEventAdapterTest {

  @Mock private ApplicationEventPublisher publisher;

  @InjectMocks private PriceSummaryEventAdapter adapter;

  @Test
  @DisplayName("Should publish price summary update requested event")
  void shouldPublishPriceSummaryUpdateRequested() {
    UUID receiptId = UUID.randomUUID();

    adapter.publishPriceSummaryUpdateRequested(receiptId);

    verify(publisher).publishEvent(new PriceSummaryUpdateRequestedEvent(receiptId));
  }
}
