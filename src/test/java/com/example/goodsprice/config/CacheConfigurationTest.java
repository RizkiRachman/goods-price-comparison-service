package com.example.goodsprice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

class CacheConfigurationTest {

  private final CacheConfiguration config = new CacheConfiguration();

  @Test
  void shouldHaveCacheNames() {
    assertEquals("stores", CacheConfiguration.STORES_CACHE);
    assertEquals("categories", CacheConfiguration.CATEGORIES_CACHE);
    assertEquals("feedback-questions", CacheConfiguration.FEEDBACK_QUESTIONS_CACHE);
  }

  @Test
  void shouldBeConfiguredWithEnableCaching() {
    var enableCaching = CacheConfiguration.class.getAnnotation(EnableCaching.class);

    assertNotNull(enableCaching);
  }

  @Test
  void shouldBeConfiguredAsSpringConfiguration() {
    var configuration = CacheConfiguration.class.getAnnotation(Configuration.class);

    assertNotNull(configuration);
  }
}
