package com.example.goodsprice.config.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimitProperties {

  private boolean enabled = true;
  private long limit = 60;
  private long windowSeconds = 60;
  private Map<String, EndpointConfig> endpoints = new ConcurrentHashMap<>();

  @Data
  public static class EndpointConfig {
    private long limit;
    private long windowSeconds;
  }
}
