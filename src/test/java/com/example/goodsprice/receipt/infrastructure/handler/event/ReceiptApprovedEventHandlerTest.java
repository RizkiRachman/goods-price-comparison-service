package com.example.goodsprice.receipt.infrastructure.handler.event;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptApprovedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptApprovedEventHandlerTest {

  @Mock private ReceiptInPort receiptInPort;

  @InjectMocks private ReceiptApprovedEventHandler handler;

  @Test
  void shouldHandleReceiptApprovedEvent() {
    var receiptId = UUID.randomUUID();
    var receipt =
        ReceiptDomain.builder()
            .id(receiptId)
            .storeName("Toko Segar")
            .extractedDataJson("{}")
            .build();

    when(receiptInPort.findById(receiptId)).thenReturn(receipt);

    var event = new ReceiptApprovedEvent(receiptId);
    handler.handle(event);

    verify(receiptInPort).findById(receiptId);
  }

  @Test
  void shouldHandleReceiptApprovedEventWhenNotFound() {
    var receiptId = UUID.randomUUID();
    when(receiptInPort.findById(receiptId)).thenThrow(new RuntimeException("Database error"));

    var event = new ReceiptApprovedEvent(receiptId);
    handler.handle(event);
    // Exception caught by try-catch in AbstractReceiptEventHandler.processReceiptEvent
  }

  @Test
  void shouldGetEventTypeLabel() {
    assert "approval".equals(handler.getEventTypeLabel());
  }
}
