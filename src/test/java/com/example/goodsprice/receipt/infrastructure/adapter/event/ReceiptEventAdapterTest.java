package com.example.goodsprice.receipt.infrastructure.adapter.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ReceiptEventAdapterTest {

  @Mock private ApplicationEventPublisher publisher;

  @InjectMocks private ReceiptEventAdapter adapter;

  @Test
  void shouldPublishReceiptUploaded() {
    var receipt =
        ReceiptDomain.builder()
            .id(UUID.randomUUID())
            .imageHash("hash123")
            .originalFilename("receipt.jpg")
            .build();

    adapter.publishReceiptUploaded(receipt);

    verify(publisher).publishEvent(any(ReceiptUploadedEvent.class));
  }

  @Test
  void shouldPublishReceiptProcessed() {
    var receipt = ReceiptDomain.builder().id(UUID.randomUUID()).build();

    adapter.publishReceiptProcessed(receipt);

    verify(publisher).publishEvent(any(ReceiptProcessedEvent.class));
  }

  @Test
  void shouldPublishReceiptApproved() {
    var receipt = ReceiptDomain.builder().id(UUID.randomUUID()).build();

    adapter.publishReceiptApproved(receipt);

    verify(publisher).publishEvent(any(ReceiptApprovedEvent.class));
  }

  @Test
  void shouldPublishReceiptCorrected() {
    var receipt = ReceiptDomain.builder().id(UUID.randomUUID()).build();

    adapter.publishReceiptCorrected(receipt);

    verify(publisher).publishEvent(any(ReceiptCorrectedEvent.class));
  }
}
