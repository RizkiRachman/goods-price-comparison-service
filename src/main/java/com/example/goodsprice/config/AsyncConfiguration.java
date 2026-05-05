package com.example.goodsprice.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration {

  @Bean(name = "receiptProcessorExecutor")
  public Executor receiptProcessorExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("receipt-processor-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setAllowCoreThreadTimeOut(true);
    executor.setKeepAliveSeconds(120);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(60);
    executor.initialize();
    log.info("Receipt processor executor initialized: core={}, max={}, queue={}",
        executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
    return executor;
  }

  @Bean(name = "receiptApproveProcessorExecutor")
  public Executor receiptApproveProcessorExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(20);
    executor.setThreadNamePrefix("receipt-approve-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setAllowCoreThreadTimeOut(true);
    executor.setKeepAliveSeconds(120);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(60);
    executor.initialize();
    log.info("Receipt approve processor executor initialized: core={}, max={}, queue={}",
        executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
    return executor;
  }
}
