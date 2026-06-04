package com.example.goodsprice.system.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.ApiVersionResponse;
import com.example.goodsprice.api.model.HealthResponse;
import com.example.goodsprice.api.model.HealthResponse.StatusEnum;
import com.example.goodsprice.api.model.HealthResponseComponents;
import com.example.goodsprice.api.model.MetricsResponse;
import com.example.goodsprice.api.model.MetricsResponseErrors;
import com.example.goodsprice.api.model.MetricsResponseRequests;
import com.example.goodsprice.api.model.MetricsResponseResponseTime;
import com.example.goodsprice.system.application.port.in.SystemInPort;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class SystemControllerTest {

  @Mock private SystemInPort systemInPort;

  @InjectMocks private SystemController controller;

  @Test
  @DisplayName("Should get API version")
  void shouldGetApiVersion() {
    var response = new ApiVersionResponse();
    response.setVersion("1.0.0");
    when(systemInPort.getApiVersion()).thenReturn(response);

    var result = controller.getApiVersion();

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals("1.0.0", result.getBody().getVersion());
    verify(systemInPort).getApiVersion();
  }

  @Test
  @DisplayName("Should get health")
  void shouldGetHealth() {
    var response = new HealthResponse();
    response.setStatus(StatusEnum.UP);
    response.setTimestamp(OffsetDateTime.now());
    var components = new HealthResponseComponents();
    components.setApi(HealthResponseComponents.ApiEnum.UP);
    response.setComponents(components);
    when(systemInPort.getHealth()).thenReturn(response);

    var result = controller.getHealth();

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(StatusEnum.UP, result.getBody().getStatus());
    verify(systemInPort).getHealth();
  }

  @Test
  @DisplayName("Should get metrics")
  void shouldGetMetrics() {
    var response = new MetricsResponse();
    response.setRequests(
        new MetricsResponseRequests().total(100).successful(90).failed(10).ratePerMinute(5.0));
    response.setResponseTime(
        new MetricsResponseResponseTime().average(0.5).p50(0.4).p95(0.8).p99(1.0));
    response.setErrors(
        new MetricsResponseErrors().validationErrors(0).notFoundErrors(1).serverErrors(0));
    response.setUptime(3600);
    response.setTimestamp(OffsetDateTime.now());
    when(systemInPort.getMetrics()).thenReturn(response);

    var result = controller.getMetrics();

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(Integer.valueOf(100), result.getBody().getRequests().getTotal());
    verify(systemInPort).getMetrics();
  }
}
