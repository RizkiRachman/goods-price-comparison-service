package com.example.goodsprice.alert.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.AlertSubscriptionRequest;
import com.example.goodsprice.api.model.AlertSubscriptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

  @Mock private AlertWebAdapter adapter;

  @InjectMocks private AlertController controller;

  @Test
  @DisplayName("Should subscribe to alert")
  void shouldSubscribe() {
    var request = new AlertSubscriptionRequest();
    request.setProductId(1L);
    request.setTargetPrice(12000.0);
    request.setNotificationMethod(AlertSubscriptionRequest.NotificationMethodEnum.EMAIL);
    request.setEmail("user@test.com");

    var response = new AlertSubscriptionResponse();
    response.setStatus(AlertSubscriptionResponse.StatusEnum.ACTIVE);
    when(adapter.subscribe(any(AlertSubscriptionRequest.class))).thenReturn(response);

    var result = controller.subscribeToAlert(request);

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(AlertSubscriptionResponse.StatusEnum.ACTIVE, result.getBody().getStatus());
    verify(adapter).subscribe(request);
  }
}
