package com.example.goodsprice.alert.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.alert.application.domain.model.AlertSubscription;
import com.example.goodsprice.alert.application.port.in.AlertInPort;
import com.example.goodsprice.alert.infrastructure.adapter.web.mapper.AlertDtoMapper;
import com.example.goodsprice.api.model.AlertSubscriptionRequest;
import com.example.goodsprice.api.model.AlertSubscriptionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertWebAdapterTest {

  @Mock private AlertInPort alertInPort;
  @Mock private AlertDtoMapper mapper;

  @InjectMocks private AlertWebAdapter adapter;

  private AlertSubscription subscription;
  private AlertSubscriptionResponse response;

  @BeforeEach
  void setUp() {
    subscription =
        AlertSubscription.builder()
            .id("sub-1")
            .productId(1L)
            .productName("Apple")
            .targetPrice(12000.0)
            .currentPrice(15000.0)
            .notificationMethod("EMAIL")
            .email("user@test.com")
            .status("ACTIVE")
            .build();

    response = new AlertSubscriptionResponse();
    response.setSubscriptionId("sub-1");
    response.setStatus(AlertSubscriptionResponse.StatusEnum.ACTIVE);
  }

  @Test
  @DisplayName("Should subscribe to price alert")
  void shouldSubscribe() {
    var request = new AlertSubscriptionRequest();
    request.setProductId(1L);
    request.setTargetPrice(12000.0);
    request.setNotificationMethod(AlertSubscriptionRequest.NotificationMethodEnum.EMAIL);
    request.setEmail("user@test.com");

    when(alertInPort.subscribe(anyLong(), anyDouble(), any(), any())).thenReturn(subscription);
    when(mapper.toResponse(any(AlertSubscription.class), any(String.class))).thenReturn(response);

    var result = adapter.subscribe(request);

    assertNotNull(result);
    assertEquals("sub-1", result.getSubscriptionId());
    verify(alertInPort).subscribe(1L, 12000.0, "email", "user@test.com");
    verify(mapper).toResponse(any(AlertSubscription.class), any(String.class));
  }

  @Test
  @DisplayName("Should handle null notification method")
  void shouldHandleNullNotificationMethod() {
    var request = new AlertSubscriptionRequest();
    request.setProductId(1L);
    request.setTargetPrice(12000.0);
    request.setNotificationMethod(null);
    request.setEmail("user@test.com");

    when(alertInPort.subscribe(anyLong(), anyDouble(), isNull(), any())).thenReturn(subscription);
    when(mapper.toResponse(any(AlertSubscription.class), any(String.class))).thenReturn(response);

    var result = adapter.subscribe(request);

    assertNotNull(result);
  }

  @Test
  @DisplayName("Should include product name and price in message")
  void shouldIncludeDetailsInMessage() {
    var request = new AlertSubscriptionRequest();
    request.setProductId(1L);
    request.setTargetPrice(12000.0);
    request.setNotificationMethod(AlertSubscriptionRequest.NotificationMethodEnum.EMAIL);
    request.setEmail("user@test.com");

    when(alertInPort.subscribe(anyLong(), anyDouble(), any(), any())).thenReturn(subscription);
    when(mapper.toResponse(any(AlertSubscription.class), any(String.class)))
        .thenAnswer(
            invocation -> {
              var msg = invocation.getArgument(1, String.class);
              var resp = new AlertSubscriptionResponse();
              resp.setMessage(msg);
              return resp;
            });

    var result = adapter.subscribe(request);

    assertNotNull(result);
    assertNotNull(result.getMessage());
    assertEquals("Alert created for Apple at 12000.00 via email", result.getMessage());
  }
}
