package com.example.goodsprice.price.infrastructure.handler.event;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.example.goodsprice.price.application.domain.service.PriceSummaryBatchService;
import com.example.goodsprice.price.infrastructure.adapter.event.PriceSummaryUpdateRequestedEvent;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceSummaryUpdateRequestedEventHandlerTest {

  @Mock private PriceSummaryBatchService batchService;

  @InjectMocks private PriceSummaryUpdateRequestedEventHandler handler;

  @Test
  @DisplayName("Should trigger batch summary update on event")
  void shouldTriggerBatchSummaryUpdate() {
    UUID receiptId = UUID.randomUUID();
    var event = new PriceSummaryUpdateRequestedEvent(receiptId);

    handler.handleEvent(event);

    verify(batchService).updateSummaries();
  }

  @Test
  @DisplayName("Should handle exception gracefully")
  void shouldHandleExceptionGracefully() {
    UUID receiptId = UUID.randomUUID();
    var event = new PriceSummaryUpdateRequestedEvent(receiptId);
    doThrow(new RuntimeException("Batch failed")).when(batchService).updateSummaries();

    handler.handleEvent(event);

    verify(batchService).updateSummaries();
  }
}
