package com.example.goodsprice.config.ratelimit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RateLimitExceededExceptionTest {

  @Test
  void shouldCreateWithLimitAndRetryAfter() {
    var ex = new RateLimitExceededException(100, 45);

    assertEquals(100, ex.getLimit());
    assertEquals(45, ex.getRetryAfterSeconds());
    assertEquals("Too many requests. Please try again after 45 seconds.", ex.getMessage());
  }

  @Test
  void shouldCreateWithZeroRetryAfter() {
    var ex = new RateLimitExceededException(10, 0);

    assertEquals(10, ex.getLimit());
    assertEquals(0, ex.getRetryAfterSeconds());
    assertEquals("Too many requests. Please try again after 0 seconds.", ex.getMessage());
  }

  @Test
  void shouldBeRuntimeException() {
    var ex = new RateLimitExceededException(1, 1);

    assertEquals(RuntimeException.class, ex.getClass().getSuperclass());
  }
}
