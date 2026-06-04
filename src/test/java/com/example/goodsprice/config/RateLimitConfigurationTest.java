package com.example.goodsprice.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.goodsprice.config.ratelimit.RateLimitInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

class RateLimitConfigurationTest {

  @Test
  void shouldRegisterRateLimitInterceptor() {
    var interceptor = mock(RateLimitInterceptor.class);
    var config = new RateLimitConfiguration(interceptor);
    var registry = mock(InterceptorRegistry.class);

    config.addInterceptors(registry);

    verify(registry).addInterceptor(interceptor);
  }
}
