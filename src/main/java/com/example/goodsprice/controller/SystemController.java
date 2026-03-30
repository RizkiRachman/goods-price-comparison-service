package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.SystemApi;
import com.example.goodsprice.api.model.ApiVersionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for system and API information endpoints.
 */
@RestController
public class SystemController implements SystemApi {

    @Override
    public ResponseEntity<ApiVersionResponse> getApiVersion() {
        ApiVersionResponse response = new ApiVersionResponse();
        response.setVersion("1.0.0");
        response.setStatus(ApiVersionResponse.StatusEnum.STABLE);
        return ResponseEntity.ok(response);
    }
}
