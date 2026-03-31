package com.example.goodsprice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for application.
 * Uses Caffeine for high-performance local caching.
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String LLM_RESPONSE_CACHE = "llm-responses";
    public static final String PRICE_SEARCH_CACHE = "price-searches";
    public static final String PRODUCT_TREND_CACHE = "product-trends";

    /**
     * Configure Caffeine cache manager with multiple cache configurations.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Configure different caches
        cacheManager.registerCustomCache(LLM_RESPONSE_CACHE, llmResponseCache());
        cacheManager.registerCustomCache(PRICE_SEARCH_CACHE, priceSearchCache());
        cacheManager.registerCustomCache(PRODUCT_TREND_CACHE, productTrendCache());

        log.info("Cache manager initialized with caches: {}",
            cacheManager.getCacheNames());

        return cacheManager;
    }

    /**
     * LLM Response Cache:
     - Cache LLM responses for receipt processing
     - Entries expire after 24 hours
     - Maximum 1000 entries
     */
    private com.github.benmanes.caffeine.cache.Cache<Object, Object> llmResponseCache() {
        return Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .recordStats()
            .build();
    }

    /**
     * Price Search Cache:
     - Cache price search results
     - Entries expire after 1 hour
     - Maximum 500 entries
     */
    private com.github.benmanes.caffeine.cache.Cache<Object, Object> priceSearchCache() {
        return Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
            .build();
    }

    /**
     * Product Trend Cache:
     - Cache product trend data
     - Entries expire after 6 hours
     - Maximum 200 entries
     */
    private com.github.benmanes.caffeine.cache.Cache<Object, Object> productTrendCache() {
        return Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(6, TimeUnit.HOURS)
            .recordStats()
            .build();
    }
}
