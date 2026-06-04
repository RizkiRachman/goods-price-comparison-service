package com.example.goodsprice.alert.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.AlertsApi;
import com.example.goodsprice.api.model.AlertSubscriptionRequest;
import com.example.goodsprice.api.model.AlertSubscriptionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlertController implements AlertsApi {

  private final AlertWebAdapter adapter;

  @Override
  public ResponseEntity<AlertSubscriptionResponse> subscribeToAlert(
      @Valid AlertSubscriptionRequest request) {
    var response = adapter.subscribe(request);
    return ResponseEntity.ok(response);
  }
}
