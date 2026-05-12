package com.example.goodsprice.common.test.extension;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goodsprice.common.test.annotation.RetryTest;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TestRetryExtension")
class TestRetryExtensionTest {

  @Test
  @DisplayName("Should pass on first attempt with @RetryTest(1)")
  @RetryTest(1)
  void shouldPassOnFirstAttempt() {
    assertTrue(true);
  }

  @Test
  @DisplayName("Should pass on first attempt with default retry count")
  @RetryTest
  void shouldPassWithDefaultRetryCount() {
    assertTrue(true);
  }

  @Test
  @DisplayName("Should pass on first attempt with @RetryTest(0)")
  @RetryTest(0)
  void shouldPassWithZeroRetries() {
    assertTrue(true);
  }
}

class RetryTestFixture {
  private RetryTestFixture() {}

  static java.util.concurrent.ConcurrentHashMap<String, AtomicInteger> counters =
      new java.util.concurrent.ConcurrentHashMap<>();

  static AtomicInteger getCounter(String testName) {
    return counters.computeIfAbsent(testName, k -> new AtomicInteger(0));
  }
}
