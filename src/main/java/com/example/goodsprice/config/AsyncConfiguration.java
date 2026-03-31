package com.example.goodsprice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for background processing.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * Executor for receipt processing.
     * Handles LLM calls in background.
     */
    @Bean(name = "receiptProcessorExecutor")
    public Executor receiptProcessorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("receipt-processor-");
        executor.initialize();
        return executor;
    }
}
