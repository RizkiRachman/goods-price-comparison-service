package com.example.goodsprice.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements SchedulingConfigurer {

  @Bean(name = "receiptProcessorExecutor")
  public Executor receiptProcessorExecutor() {
    return createExecutor("receipt-processor-", 3, 10, 50, 60);
  }

  @Bean(name = "receiptApproveProcessorExecutor")
  public Executor receiptApproveProcessorExecutor() {
    return createExecutor("receipt-approve-", 2, 5, 20, 60);
  }

  @Bean(name = "activityLogExecutor")
  public Executor activityLogExecutor() {
    return createExecutor("activity-log-", 2, 5, 100, 30);
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(scheduledTaskExecutor());
  }

  @Bean(destroyMethod = "shutdown")
  public ThreadPoolTaskScheduler scheduledTaskExecutor() {
    var scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(4);
    scheduler.setThreadNamePrefix("scheduled-task-");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(60);
    scheduler.initialize();
    log.info("scheduled-task scheduler initialized: pool=4");
    return scheduler;
  }

  private static Executor createExecutor(
      String threadPrefix, int coreSize, int maxSize, int queueCapacity, int awaitSeconds) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(coreSize);
    executor.setMaxPoolSize(maxSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix(threadPrefix);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setAllowCoreThreadTimeOut(true);
    executor.setKeepAliveSeconds(120);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(awaitSeconds);
    executor.initialize();
    log.info(
        "{}executor initialized: core={}, max={}, queue={}",
        threadPrefix,
        executor.getCorePoolSize(),
        executor.getMaxPoolSize(),
        executor.getQueueCapacity());
    return executor;
  }
}
