package com.example.goodsprice.receipt.infrastructure.handler.event;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractReceiptEventHandlerTest {

  @Mock private ReceiptInPort receiptInPort;
  @Mock private ReceiptEventHandlerHelper helper;

  @InjectMocks private ReceiptProcessedEventHandler handler;

  @Test
  void shouldProcessReceiptEventSuccessfully() {
    var receiptId = UUID.randomUUID();
    var extractedDataJson = "{\"items\":[{\"productName\":\"Apple\"}]}";
    var receipt =
        ReceiptDomain.builder()
            .id(receiptId)
            .storeName("Toko Segar")
            .receiptDate("2026-05-08")
            .extractedDataJson(extractedDataJson)
            .build();

    when(receiptInPort.findById(receiptId)).thenReturn(receipt);

    handler.processReceiptEvent(receiptId);

    verify(helper).resolveStore("Toko Segar");
    verify(helper).parseDate("2026-05-08");
  }

  @Test
  void shouldSkipWhenItemsAreEmpty() {
    var receiptId = UUID.randomUUID();
    var receipt = ReceiptDomain.builder().id(receiptId).extractedDataJson("{}").build();

    when(receiptInPort.findById(receiptId)).thenReturn(receipt);

    handler.processReceiptEvent(receiptId);

    // Method returns early when items are empty - no helper interaction
  }

  @Test
  void shouldHandleExceptionGracefully() {
    var receiptId = UUID.randomUUID();
    when(receiptInPort.findById(receiptId)).thenThrow(new RuntimeException("Database error"));

    handler.processReceiptEvent(receiptId);
    // Exception caught by try-catch, no rethrow
  }
}
