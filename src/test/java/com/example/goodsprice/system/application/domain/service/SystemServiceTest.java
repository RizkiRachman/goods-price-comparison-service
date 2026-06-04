package com.example.goodsprice.system.application.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemServiceTest {

  @InjectMocks private SystemService systemService;

  @Test
  void getApiVersion() {
    var result = systemService.getApiVersion();
    assertThat(result).isNotNull();
    assertThat(result.getVersion()).isNotNull();
    assertThat(result.getStatus()).isNotNull();
  }

  @Test
  void getHealth() {
    var result = systemService.getHealth();
    assertThat(result).isNotNull();
    assertThat(result.getTimestamp()).isNotNull();
    assertThat(result.getVersion()).isNotNull();
    assertThat(result.getStatus()).isNotNull();
    assertThat(result.getComponents()).isNotNull();
  }

  @Test
  void getMetrics() {
    var result = systemService.getMetrics();
    assertThat(result).isNotNull();
    assertThat(result.getRequests()).isNotNull();
    assertThat(result.getResponseTime()).isNotNull();
    assertThat(result.getErrors()).isNotNull();
    assertThat(result.getTimestamp()).isNotNull();
  }
}
