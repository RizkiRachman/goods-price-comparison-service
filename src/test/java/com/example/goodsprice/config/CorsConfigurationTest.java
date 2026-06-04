package com.example.goodsprice.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CorsConfigurationTest {

  @Mock private CorsProperties corsProperties;

  @Test
  void shouldCreateCorsFilter() {
    when(corsProperties.getAllowedMethods()).thenReturn(List.of("GET", "POST"));
    when(corsProperties.getAllowedHeaders()).thenReturn(List.of("*"));
    when(corsProperties.isAllowCredentials()).thenReturn(true);

    var config = new CorsConfiguration(corsProperties);
    var bean = config.corsFilter();

    assertNotNull(bean);
    assertNotNull(bean.getFilter());
    assertTrue(bean.getOrder() < 0); // HIGHEST_PRECEDENCE
  }
}
