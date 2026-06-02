package com.example.goodsprice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {
  public static final String STORES_CACHE = "stores";
  public static final String CATEGORIES_CACHE = "categories";
  public static final String FEEDBACK_QUESTIONS_CACHE = "feedback-questions";
}
