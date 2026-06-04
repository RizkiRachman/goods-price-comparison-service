package com.example.goodsprice.receipt.infrastructure.handler.event;

import static org.mockito.Mockito.verify;

import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptUploadedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptUploadedEventHandlerTest {

  @Mock private ReceiptInPort receiptInPort;

  @InjectMocks private ReceiptUploadedEventHandler handler;

  @Test
  void shouldHandleReceiptUploadedEvent() {
    var receiptId = UUID.randomUUID();
    var event = new ReceiptUploadedEvent(receiptId, "hash123", "receipt.jpg");

    handler.handle(event);

    verify(receiptInPort).process(receiptId, null);
  }
}
