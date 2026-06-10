package com.example.goodsprice.receipt.infrastructure.handler.event;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.example.goodsprice.price.application.port.out.PriceSummaryEventOutPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptCorrectedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptCorrectedPriceCalcHandlerTest {

  @Mock private PriceSummaryEventOutPort priceSummaryEventOutPort;

  @InjectMocks private ReceiptCorrectedPriceCalcHandler handler;

  @Test
  void shouldTriggerPriceCalculationAfterCorrection() {
    var receiptId = UUID.randomUUID();
    var event = new ReceiptCorrectedEvent(receiptId);

    handler.handleEvent(event);

    verify(priceSummaryEventOutPort).publishPriceSummaryUpdateRequested(receiptId);
  }

  @Test
  void shouldHandleExceptionGracefully() {
    var receiptId = UUID.randomUUID();
    var event = new ReceiptCorrectedEvent(receiptId);
    doThrow(new RuntimeException("Price calc error"))
        .when(priceSummaryEventOutPort)
        .publishPriceSummaryUpdateRequested(receiptId);

    handler.handleEvent(event);
    // Exception caught by try-catch, no rethrow
  }
}
