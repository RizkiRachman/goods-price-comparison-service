package com.example.goodsprice.system;

import com.example.goodsprice.api.controller.SystemApi;
import com.example.goodsprice.api.model.ApiVersionResponse;
import com.example.goodsprice.api.model.HealthResponse;
import com.example.goodsprice.api.model.HealthResponse.StatusEnum;
import com.example.goodsprice.api.model.HealthResponseComponents;
import com.example.goodsprice.api.model.MetricsResponse;
import com.example.goodsprice.api.model.MetricsResponseErrors;
import com.example.goodsprice.api.model.MetricsResponseRequests;
import com.example.goodsprice.api.model.MetricsResponseResponseTime;
import com.example.goodsprice.common.constant.AppConstants;
import java.lang.management.ManagementFactory;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController implements SystemApi {

  @Override
  public ResponseEntity<ApiVersionResponse> getApiVersion() {
    var response = new ApiVersionResponse();
    response.setVersion(AppConstants.API_VERSION);
    response.setFullVersion(AppConstants.API_VERSION);
    response.setStatus(ApiVersionResponse.StatusEnum.STABLE);
    response.setSupportedVersions(List.of());
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<HealthResponse> getHealth() {
    var response = new HealthResponse();
    var components = new HealthResponseComponents();
    components.setApi(HealthResponseComponents.ApiEnum.UP);
    components.setDatabase(HealthResponseComponents.DatabaseEnum.UP);
    components.setOcr(HealthResponseComponents.OcrEnum.UP);
    response.setComponents(components);
    response.setStatus(StatusEnum.UP);
    response.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
    response.setVersion(AppConstants.API_VERSION);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<MetricsResponse> getMetrics() {
    var uptime = (int) (ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
    var response = new MetricsResponse();
    response.setUptime(uptime);
    response.setRequests(
        new MetricsResponseRequests().total(0).successful(0).failed(0).ratePerMinute(0.0));
    response.setResponseTime(
        new MetricsResponseResponseTime().average(0.0).p50(0.0).p95(0.0).p99(0.0));
    response.setErrors(
        new MetricsResponseErrors().validationErrors(0).notFoundErrors(0).serverErrors(0));
    response.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
    return ResponseEntity.ok(response);
  }
}
