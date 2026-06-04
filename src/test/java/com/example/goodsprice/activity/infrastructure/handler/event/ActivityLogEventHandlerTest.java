package com.example.goodsprice.activity.infrastructure.handler.event;

import static org.mockito.Mockito.verify;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogDomain;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.activity.application.port.in.ActivityLogInPort;
import com.example.goodsprice.activity.infrastructure.adapter.event.ActivityLoggedEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityLogEventHandlerTest {

  @Mock private ActivityLogInPort activityLogInPort;

  @InjectMocks private ActivityLogEventHandler handler;

  private ActivityLogDomain domain;
  private ActivityLoggedEvent event;

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
    event = new ActivityLoggedEvent(domain);
  }

  @Test
  @DisplayName("Should persist activity log when event is handled")
  void shouldPersistActivityLog() {
    handler.handle(event);

    verify(activityLogInPort).log(domain);
  }
}
