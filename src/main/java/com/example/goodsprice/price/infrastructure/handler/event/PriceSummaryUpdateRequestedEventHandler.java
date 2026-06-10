package com.example.goodsprice.price.infrastructure.handler.event;

import com.example.goodsprice.price.application.domain.service.PriceSummaryBatchService;
import com.example.goodsprice.price.infrastructure.adapter.event.PriceSummaryUpdateRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceSummaryUpdateRequestedEventHandler
    extends AbstractAsyncPriceCalcHandler<PriceSummaryUpdateRequestedEvent> {
  private final PriceSummaryBatchService batchService;

  @Override
  protected void doExecute(PriceSummaryUpdateRequestedEvent event) {
    batchService.updateSummaries();
  }
}
