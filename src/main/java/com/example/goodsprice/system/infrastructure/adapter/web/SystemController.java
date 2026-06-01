package com.example.goodsprice.system.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.SystemApi;
import com.example.goodsprice.api.model.ApiVersionResponse;
import com.example.goodsprice.api.model.HealthResponse;
import com.example.goodsprice.api.model.MetricsResponse;
import com.example.goodsprice.system.application.port.in.SystemInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SystemController implements SystemApi {

  private final SystemInPort systemInPort;

  @Override
  public ResponseEntity<ApiVersionResponse> getApiVersion() {
    return ResponseEntity.ok(systemInPort.getApiVersion());
  }

  @Override
  public ResponseEntity<HealthResponse> getHealth() {
    return ResponseEntity.ok(systemInPort.getHealth());
  }

  @Override
  public ResponseEntity<MetricsResponse> getMetrics() {
    return ResponseEntity.ok(systemInPort.getMetrics());
  }
}
