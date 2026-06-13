package com.example.goodsprice.alert.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.AlertSubscriptionRequest;
import com.example.goodsprice.api.model.AlertSubscriptionResponse;
import com.example.goodsprice.common.web.AbstractControllerWebMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class AlertControllerWebMvcTest extends AbstractControllerWebMvcTest {

  @Mock private AlertWebAdapter adapter;

  @Override
  protected Object getController() {
    return new AlertController(adapter);
  }

  @Test
  @DisplayName("POST /v1/alerts/subscribe should return 200 OK")
  void shouldSubscribeReturns200() throws Exception {
    var response = new AlertSubscriptionResponse();
    response.setSubscriptionId("sub-123");
    response.setStatus(AlertSubscriptionResponse.StatusEnum.ACTIVE);
    when(adapter.subscribe(any(AlertSubscriptionRequest.class))).thenReturn(response);

    var request = new AlertSubscriptionRequest();
    request.setProductId(1L);
    request.setTargetPrice(15000.0);
    request.setNotificationMethod(AlertSubscriptionRequest.NotificationMethodEnum.EMAIL);
    request.setEmail("user@test.com");

    mockMvc
        .perform(
            post("/v1/alerts/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.subscriptionId").value("sub-123"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  @DisplayName("POST /v1/alerts/subscribe should return 400 when productId is null")
  void shouldReturn400WhenProductIdIsNull() throws Exception {
    String invalidJson =
        """
        {"targetPrice": 15000.0}
        """;

    mockMvc
        .perform(
            post("/v1/alerts/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /v1/alerts/subscribe should return 400 when request body is invalid")
  void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/v1/alerts/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
        .andExpect(status().isBadRequest());
  }
}
