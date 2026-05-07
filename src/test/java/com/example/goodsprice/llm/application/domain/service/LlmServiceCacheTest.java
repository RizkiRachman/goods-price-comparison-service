package com.example.goodsprice.llm.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LlmServiceCacheTest {

  @Autowired private LlmService llmService;

  @Autowired private CacheManager cacheManager;

  @Test
  @DisplayName("Should cache LLM responses for same image")
  void shouldCacheResponsesForSameImage() {
    if (!llmService.isAvailable()) {
      System.out.println("Skipping test - provider not available");
      return;
    }

    String imageBase64 = "base64encodedimagedata123";
    Map<String, Object> result1 = llmService.extractReceipt(imageBase64);
    Map<String, Object> result2 = llmService.extractReceipt(imageBase64);

    assertNotNull(result1);
    assertNotNull(result2);
    assertEquals(result1, result2);
  }

  @Test
  @DisplayName("Should generate consistent hash for same image")
  void shouldGenerateConsistentHash() {
    String imageBase64 = "testimage123";

    String hash1 = llmService.generateImageHash(imageBase64);
    String hash2 = llmService.generateImageHash(imageBase64);

    assertEquals(hash1, hash2);
    assertEquals(64, hash1.length());
  }

  @Test
  @DisplayName("Should generate different hashes for different images")
  void shouldGenerateDifferentHashesForDifferentImages() {
    String image1 = "image1data";
    String image2 = "image2data";

    String hash1 = llmService.generateImageHash(image1);
    String hash2 = llmService.generateImageHash(image2);

    assertNotEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should have cache manager configured")
  void shouldHaveCacheManagerConfigured() {
    assertNotNull(cacheManager);
    assertNotNull(cacheManager.getCache("llm-responses"));
  }
}
