package com.example.goodsprice.system.application.port.in;

import com.example.goodsprice.api.model.ApiVersionResponse;
import com.example.goodsprice.api.model.HealthResponse;
import com.example.goodsprice.api.model.MetricsResponse;

public interface SystemInPort {
  ApiVersionResponse getApiVersion();

  HealthResponse getHealth();

  MetricsResponse getMetrics();
}
