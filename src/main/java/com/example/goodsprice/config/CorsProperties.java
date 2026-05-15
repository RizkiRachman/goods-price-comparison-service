package com.example.goodsprice.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

  private List<String> allowedOrigins = List.of("http://localhost:3000");
  private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
  private List<String> allowedHeaders = List.of("*");
  private boolean allowCredentials = true;
}
