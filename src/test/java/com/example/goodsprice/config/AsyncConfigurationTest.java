package com.example.goodsprice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Async Configuration Tests")
class AsyncConfigurationTest {

  @Autowired
  @Qualifier("receiptProcessorExecutor")
  private Executor receiptProcessorExecutor;

  @Autowired
  @Qualifier("receiptApproveProcessorExecutor")
  private Executor receiptApproveProcessorExecutor;

  @Test
  @DisplayName("Should have receipt processor executor bean configured")
  void shouldHaveReceiptProcessorExecutor() {
    assertNotNull(receiptProcessorExecutor);
    assertTrue(receiptProcessorExecutor instanceof ThreadPoolTaskExecutor);
    var executor = (ThreadPoolTaskExecutor) receiptProcessorExecutor;
    assertEquals(3, executor.getCorePoolSize());
    assertEquals(10, executor.getMaxPoolSize());
    assertEquals(50, executor.getQueueCapacity());
    assertTrue(executor.getThreadNamePrefix().contains("receipt-processor-"));
  }

  @Test
  @DisplayName("Should have receipt approve processor executor bean configured")
  void shouldHaveReceiptApproveProcessorExecutor() {
    assertNotNull(receiptApproveProcessorExecutor);
    assertTrue(receiptApproveProcessorExecutor instanceof ThreadPoolTaskExecutor);
    var executor = (ThreadPoolTaskExecutor) receiptApproveProcessorExecutor;
    assertEquals(2, executor.getCorePoolSize());
    assertEquals(5, executor.getMaxPoolSize());
    assertEquals(20, executor.getQueueCapacity());
    assertTrue(executor.getThreadNamePrefix().contains("receipt-approve-"));
  }

  @Test
  @DisplayName("Should execute task asynchronously on receipt processor thread")
  void shouldExecuteTaskOnReceiptProcessorThread() throws Exception {
    var testThreadName = Thread.currentThread().getName();
    var asyncThreadName = new AtomicReference<String>();

    var future =
        CompletableFuture.runAsync(
            () -> asyncThreadName.set(Thread.currentThread().getName()), receiptProcessorExecutor);

    future.get();

    assertNotNull(asyncThreadName.get());
    assertTrue(asyncThreadName.get().startsWith("receipt-processor-"));
  }

  @Test
  @DisplayName("Should execute task asynchronously on receipt approve processor thread")
  void shouldExecuteTaskOnReceiptApproveProcessorThread() throws Exception {
    var testThreadName = Thread.currentThread().getName();
    var asyncThreadName = new AtomicReference<String>();

    var future =
        CompletableFuture.runAsync(
            () -> asyncThreadName.set(Thread.currentThread().getName()),
            receiptApproveProcessorExecutor);

    future.get();

    assertNotNull(asyncThreadName.get());
    assertTrue(asyncThreadName.get().startsWith("receipt-approve-"));
  }
}
