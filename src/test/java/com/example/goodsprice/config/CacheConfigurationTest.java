package com.example.goodsprice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

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
    var enableCaching =
        CacheConfiguration.class.getAnnotation(
            org.springframework.cache.annotation.EnableCaching.class);

    assertNotNull(enableCaching);
  }

  @Test
  void shouldBeConfiguredAsSpringConfiguration() {
    var configuration =
        CacheConfiguration.class.getAnnotation(
            org.springframework.context.annotation.Configuration.class);

    assertNotNull(configuration);
  }
}
