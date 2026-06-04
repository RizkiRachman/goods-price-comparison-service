package com.example.goodsprice.price.infrastructure.scheduler;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.example.goodsprice.price.application.domain.service.PriceSummaryBatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceSummaryBatchSchedulerTest {

  @Mock private PriceSummaryBatchService batchService;

  @InjectMocks private PriceSummaryBatchScheduler scheduler;

  @Test
  @DisplayName("Should trigger batch summary update on scheduled run")
  void shouldTriggerBatchSummaryUpdate() {
    scheduler.scheduledSummaryUpdate();

    verify(batchService).updateSummaries();
  }

  @Test
  @DisplayName("Should handle exception gracefully during scheduled run")
  void shouldHandleExceptionGracefully() {
    doThrow(new RuntimeException("Scheduled update failed")).when(batchService).updateSummaries();

    scheduler.scheduledSummaryUpdate();

    verify(batchService).updateSummaries();
  }
}
