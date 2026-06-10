package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.price.application.port.out.PriceSummaryEventOutPort;
import com.example.goodsprice.price.infrastructure.handler.event.AbstractAsyncPriceCalcHandler;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptCorrectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptCorrectedPriceCalcHandler
    extends AbstractAsyncPriceCalcHandler<ReceiptCorrectedEvent> {

  private final PriceSummaryEventOutPort priceSummaryEventOutPort;

  @Override
  protected void doExecute(ReceiptCorrectedEvent event) {
    priceSummaryEventOutPort.publishPriceSummaryUpdateRequested(event.receiptId());
  }
}
