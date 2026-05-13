package com.example.goodsprice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfiguration {

  public static final String LLM_RESPONSE_CACHE = "llm-responses";
  public static final String PRICE_SEARCH_CACHE = "price-searches";
  public static final String PRODUCT_TREND_CACHE = "product-trends";
  public static final String RECEIPT_PROCESSING_CACHE = "receipt-processing";
  public static final String CATEGORIES_CACHE = "categories";
  public static final String UNITS_CACHE = "units";
  public static final String BILL_SPLIT_CACHE = "bill-splits";

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.registerCustomCache(LLM_RESPONSE_CACHE, llmResponseCache());
    cacheManager.registerCustomCache(PRICE_SEARCH_CACHE, priceSearchCache());
    cacheManager.registerCustomCache(PRODUCT_TREND_CACHE, productTrendCache());
    cacheManager.registerCustomCache(RECEIPT_PROCESSING_CACHE, receiptProcessingCache());
    cacheManager.registerCustomCache(CATEGORIES_CACHE, categoriesCache());
    cacheManager.registerCustomCache(UNITS_CACHE, unitsCache());
    cacheManager.registerCustomCache(BILL_SPLIT_CACHE, billSplitCache());
    log.info("Cache manager initialized with caches: {}", cacheManager.getCacheNames());
    return cacheManager;
  }

  private com.github.benmanes.caffeine.cache.Cache<Object, Object> llmResponseCache() {
    return Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(24, TimeUnit.HOURS)
        .recordStats()
        .build();
  }

  private com.github.benmanes.caffeine.cache.Cache<Object, Object> priceSearchCache() {
    return Caffeine.newBuilder()
        .maximumSize(500)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .recordStats()
        .build();
  }

  private com.github.benmanes.caffeine.cache.Cache<Object, Object> productTrendCache() {
    return Caffeine.newBuilder()
        .maximumSize(200)
        .expireAfterWrite(6, TimeUnit.HOURS)
        .recordStats()
        .build();
  }

  private com.github.benmanes.caffeine.cache.Cache<Object, Object> receiptProcessingCache() {
    return Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(24, TimeUnit.HOURS)
        .recordStats()
        .build();
  }

  private com.github.benmanes.caffeine.cache.Cache<Object, Object> categoriesCache() {
    return Caffeine.newBuilder()
        .maximumSize(200)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .recordStats()
        .build();
  }

  private com.github.benmanes.caffeine.cache.Cache<Object, Object> unitsCache() {
    return Caffeine.newBuilder()
        .maximumSize(200)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .recordStats()
        .build();
  }

  private com.github.benmanes.caffeine.cache.Cache<Object, Object> billSplitCache() {
    return Caffeine.newBuilder()
        .maximumSize(500)
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .recordStats()
        .build();
  }
}
