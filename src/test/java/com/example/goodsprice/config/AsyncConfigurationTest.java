package com.example.goodsprice.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Executor;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

class AsyncConfigurationTest {

  private final AsyncConfiguration config = new AsyncConfiguration();

  @Test
  void shouldCreateReceiptProcessorExecutor() {
    var executor = config.receiptProcessorExecutor();

    assertNotNull(executor);
    assertTrue(executor instanceof Executor);
  }

  @Test
  void shouldCreateReceiptApproveProcessorExecutor() {
    var executor = config.receiptApproveProcessorExecutor();

    assertNotNull(executor);
    assertTrue(executor instanceof Executor);
  }

  @Test
  void shouldCreateActivityLogExecutor() {
    var executor = config.activityLogExecutor();

    assertNotNull(executor);
    assertTrue(executor instanceof Executor);
  }

  @Test
  void shouldCreateScheduledTaskExecutor() {
    var scheduler = config.scheduledTaskExecutor();

    assertNotNull(scheduler);
    assertTrue(scheduler instanceof ThreadPoolTaskScheduler);
  }
}
