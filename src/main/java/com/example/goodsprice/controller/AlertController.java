package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.AlertsApi;
import com.example.goodsprice.api.model.AlertSubscriptionRequest;
import com.example.goodsprice.api.model.AlertSubscriptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for price alert subscriptions.
 */
@RestController
public class AlertController implements AlertsApi {

    @Override
    public ResponseEntity<AlertSubscriptionResponse> subscribeToAlert(AlertSubscriptionRequest request) {
        // TODO: Implement alert subscription logic
        AlertSubscriptionResponse response = new AlertSubscriptionResponse();
        response.setStatus(AlertSubscriptionResponse.StatusEnum.ACTIVE);
        return ResponseEntity.ok(response);
    }
}
