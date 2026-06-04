package com.example.goodsprice.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
@RequiredArgsConstructor
public class CorsConfiguration {

  private final CorsProperties corsProperties;

  @Value("${cors.allowed-origin-patterns}")
  private List<String> allowedOriginPatterns;

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter() {
    var config = new org.springframework.web.cors.CorsConfiguration();
    config.setAllowedOriginPatterns(allowedOriginPatterns);
    config.setAllowedMethods(corsProperties.getAllowedMethods());
    config.setAllowedHeaders(corsProperties.getAllowedHeaders());
    config.setAllowCredentials(corsProperties.isAllowCredentials());

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    var bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }
}
