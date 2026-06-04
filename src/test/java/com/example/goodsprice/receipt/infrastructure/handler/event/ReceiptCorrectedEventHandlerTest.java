package com.example.goodsprice.receipt.infrastructure.handler.event;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptCorrectedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptCorrectedEventHandlerTest {

  @Mock private ReceiptInPort receiptInPort;

  @InjectMocks private ReceiptCorrectedEventHandler handler;

  @Test
  void shouldHandleReceiptCorrectedEvent() {
    var receiptId = UUID.randomUUID();
    var receipt =
        ReceiptDomain.builder()
            .id(receiptId)
            .storeName("Toko Segar")
            .extractedDataJson("{}")
            .build();

    when(receiptInPort.findById(receiptId)).thenReturn(receipt);

    var event = new ReceiptCorrectedEvent(receiptId);
    handler.handle(event);

    verify(receiptInPort).findById(receiptId);
  }

  @Test
  void shouldGetEventTypeLabel() {
    assert "correction".equals(handler.getEventTypeLabel());
  }
}
