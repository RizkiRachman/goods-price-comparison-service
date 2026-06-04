package com.example.goodsprice.activity.infrastructure.adapter.event;

import static org.mockito.Mockito.verify;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ActivityLogEventAdapterTest {

  @Mock private ApplicationEventPublisher publisher;

  @InjectMocks private ActivityLogEventAdapter eventAdapter;

  private ActivityLogDomain domain;

  @BeforeEach
  void setUp() {
    domain =
        ActivityLogDomain.builder()
            .id(UUID.randomUUID())
            .type(ActivityLogType.PRODUCT)
            .action(ActivityLogAction.CREATE)
            .description("Product created")
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("Should publish ActivityLoggedEvent when logging activity")
  void shouldPublishEvent() {
    eventAdapter.publishLogged(domain);

    verify(publisher).publishEvent(new ActivityLoggedEvent(domain));
  }
}
