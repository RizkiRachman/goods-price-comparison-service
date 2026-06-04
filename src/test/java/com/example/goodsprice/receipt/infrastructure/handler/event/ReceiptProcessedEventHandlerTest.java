package com.example.goodsprice.receipt.infrastructure.handler.event;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptProcessedEvent;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptProcessedEventHandlerTest {

  @Mock private ReceiptInPort receiptInPort;
  @Mock private StoreRepositoryPort storeRepository;

  @InjectMocks private ReceiptProcessedEventHandler handler;

  @Test
  void shouldHandleReceiptProcessedEvent() {
    var receiptId = UUID.randomUUID();
    var items = List.of(Map.<String, Object>of("productName", "Apple"));
    var receipt =
        ReceiptDomain.builder()
            .id(receiptId)
            .storeName("Toko Segar")
            .receiptDate("2026-05-08")
            .extractedDataJson("[{\"productName\":\"Apple\"}]")
            .build();

    when(receiptInPort.findById(receiptId)).thenReturn(receipt);

    var event = new ReceiptProcessedEvent(receiptId);
    handler.handle(event);

    verify(receiptInPort).findById(receiptId);
  }

  @Test
  void shouldGetEventTypeLabel() {
    var label = handler.getEventTypeLabel();
    assert "processing".equals(label);
  }
}
