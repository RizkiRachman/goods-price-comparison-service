package com.example.goodsprice.config.ratelimit;

import com.example.goodsprice.common.constant.HttpHeaderConstants;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

  private final RateLimitProperties properties;
  private final RateLimitBucketStore bucketStore;

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler)
      throws Exception {
    if (!properties.isEnabled() || !(handler instanceof HandlerMethod method)) {
      return true;
    }

    String clientIp = extractClientIp(request);
    String endpoint = request.getRequestURI();
    long limit = resolveLimit(method, endpoint);
    long windowSeconds = resolveWindowSeconds(method, endpoint);

    String bucketKey = clientIp + "::" + endpoint;
    Bucket bucket = bucketStore.getOrCreate(bucketKey, limit, windowSeconds);
    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

    response.addHeader(HttpHeaderConstants.X_RATE_LIMIT_LIMIT, String.valueOf(limit));

    if (probe.isConsumed()) {
      response.addHeader(
          HttpHeaderConstants.X_RATE_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
      return true;
    }

    long retryAfterSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
    throw new RateLimitExceededException(limit, retryAfterSeconds);
  }

  private long resolveLimit(HandlerMethod method, String endpoint) {
    RateLimit annotation = method.getMethodAnnotation(RateLimit.class);
    if (Objects.nonNull(annotation) && annotation.limit() > 0) {
      return annotation.limit();
    }
    RateLimitProperties.EndpointConfig config = properties.getEndpoints().get(endpoint);
    if (Objects.nonNull(config) && config.getLimit() > 0) {
      return config.getLimit();
    }
    return properties.getLimit();
  }

  private long resolveWindowSeconds(HandlerMethod method, String endpoint) {
    RateLimit annotation = method.getMethodAnnotation(RateLimit.class);
    if (Objects.nonNull(annotation) && annotation.windowSeconds() > 0) {
      return annotation.windowSeconds();
    }
    RateLimitProperties.EndpointConfig config = properties.getEndpoints().get(endpoint);
    if (Objects.nonNull(config) && config.getWindowSeconds() > 0) {
      return config.getWindowSeconds();
    }
    return properties.getWindowSeconds();
  }

  private String extractClientIp(HttpServletRequest request) {
    String forwarded = request.getHeader(HttpHeaderConstants.X_FORWARDED_FOR);
    if (Objects.nonNull(forwarded) && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
