package com.example.goodsprice.config.ratelimit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.common.constant.HttpHeaderConstants;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
class RateLimitInterceptorTest {

  @Mock private RateLimitProperties properties;
  @Mock private RateLimitBucketStore bucketStore;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private HandlerMethod handlerMethod;
  @Mock private Bucket bucket;
  @Mock private ConsumptionProbe probe;

  private RateLimitInterceptor interceptor;

  @BeforeEach
  void setUp() {
    interceptor = new RateLimitInterceptor(properties, bucketStore);
  }

  @Test
  void shouldPassThroughWhenDisabled() throws Exception {
    when(properties.isEnabled()).thenReturn(false);

    var result = interceptor.preHandle(request, response, new Object());

    assertTrue(result);
  }

  @Test
  void shouldPassThroughWhenNotHandlerMethod() throws Exception {
    when(properties.isEnabled()).thenReturn(true);

    var result = interceptor.preHandle(request, response, new Object());

    assertTrue(result);
  }

  @Test
  void shouldAllowRequestWhenUnderLimit() throws Exception {
    when(properties.isEnabled()).thenReturn(true);
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(request.getRequestURI()).thenReturn("/api/test");
    when(properties.getLimit()).thenReturn(60L);
    when(properties.getWindowSeconds()).thenReturn(60L);
    when(bucketStore.getOrCreate(anyString(), anyLong(), anyLong())).thenReturn(bucket);
    when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
    when(probe.isConsumed()).thenReturn(true);
    when(probe.getRemainingTokens()).thenReturn(59L);

    var result = interceptor.preHandle(request, response, handlerMethod);

    assertTrue(result);
    verify(response).addHeader(HttpHeaderConstants.X_RATE_LIMIT_LIMIT, "60");
    verify(response).addHeader(HttpHeaderConstants.X_RATE_LIMIT_REMAINING, "59");
  }

  @Test
  void shouldThrowWhenOverLimit() {
    when(properties.isEnabled()).thenReturn(true);
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(request.getRequestURI()).thenReturn("/api/test");
    when(properties.getLimit()).thenReturn(60L);
    when(properties.getWindowSeconds()).thenReturn(60L);
    when(bucketStore.getOrCreate(anyString(), anyLong(), anyLong())).thenReturn(bucket);
    when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
    when(probe.isConsumed()).thenReturn(false);
    when(probe.getNanosToWaitForRefill()).thenReturn(30_000_000_000L);

    assertThrows(
        RateLimitExceededException.class,
        () -> interceptor.preHandle(request, response, handlerMethod));
  }

  @Test
  void shouldUseXForwardedForHeader() throws Exception {
    when(properties.isEnabled()).thenReturn(true);
    when(request.getHeader(HttpHeaderConstants.X_FORWARDED_FOR))
        .thenReturn("192.168.1.1, 10.0.0.1");
    when(request.getRequestURI()).thenReturn("/api/test");
    when(properties.getLimit()).thenReturn(60L);
    when(properties.getWindowSeconds()).thenReturn(60L);
    when(bucketStore.getOrCreate(anyString(), anyLong(), anyLong())).thenReturn(bucket);
    when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
    when(probe.isConsumed()).thenReturn(true);
    when(probe.getRemainingTokens()).thenReturn(59L);

    var result = interceptor.preHandle(request, response, handlerMethod);

    assertTrue(result);
  }

  @Test
  void shouldUseMethodAnnotationLimitWhenPresent() throws Exception {
    when(properties.isEnabled()).thenReturn(true);
    when(request.getRemoteAddr()).thenReturn("10.0.0.1");
    when(request.getRequestURI()).thenReturn("/api/test");

    var annotation =
        new RateLimit() {
          @Override
          public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return RateLimit.class;
          }

          @Override
          public long limit() {
            return 5;
          }

          @Override
          public long windowSeconds() {
            return 10;
          }
        };
    when(handlerMethod.getMethodAnnotation(RateLimit.class)).thenReturn(annotation);
    when(bucketStore.getOrCreate(anyString(), anyLong(), anyLong())).thenReturn(bucket);
    when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
    when(probe.isConsumed()).thenReturn(true);
    when(probe.getRemainingTokens()).thenReturn(4L);

    var result = interceptor.preHandle(request, response, handlerMethod);

    assertTrue(result);
    verify(response).addHeader(HttpHeaderConstants.X_RATE_LIMIT_LIMIT, "5");
  }
}
