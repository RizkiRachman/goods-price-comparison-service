package com.example.goodsprice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async configuration for background processing.
 * Optimized to prevent thread starvation and connection pool exhaustion.
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * Executor for receipt processing.
     * Handles LLM calls in background.
     * 
     * Configuration optimized for:
     * - Preventing thread starvation
     * - Handling long-running LLM calls
     * - Graceful degradation under load
     */
    @Bean(name = "receiptProcessorExecutor")
    public Executor receiptProcessorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool: threads kept alive even when idle
        executor.setCorePoolSize(3);
        
        // Max pool: maximum threads under heavy load
        executor.setMaxPoolSize(10);
        
        // Queue capacity: buffer for pending tasks
        executor.setQueueCapacity(50);
        
        // Thread naming for debugging
        executor.setThreadNamePrefix("receipt-processor-");
        
        // Rejection policy: run in caller's thread when pool is full
        // Prevents losing tasks but may block the HTTP thread
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // Allow core threads to timeout (destroy idle threads)
        executor.setAllowCoreThreadTimeOut(true);
        
        // Thread keep-alive time: how long to keep excess idle threads
        executor.setKeepAliveSeconds(120);
        
        // Wait for tasks to complete on shutdown (graceful shutdown)
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        
        log.info("Receipt processor executor initialized: core={}, max={}, queue={}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
}
