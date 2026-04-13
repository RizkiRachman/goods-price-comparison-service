package com.example.goodsprice.module.llm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.cache.CacheManager;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LLM Service caching functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
class LLMServiceCacheTest {

    @Autowired
    private LLMService llmService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Should cache LLM responses for same image")
    void shouldCacheResponsesForSameImage() {
        // Skip if provider not available
        if (!llmService.isAvailable()) {
            System.out.println("Skipping test - provider not available");
            return;
        }
        
        // Given - same image data
        String imageBase64 = "base64encodedimagedata123";

        // When - call twice with same image
        Map<String, Object> result1 = llmService.extractReceipt(imageBase64);
        Map<String, Object> result2 = llmService.extractReceipt(imageBase64);

        // Then - results should be identical (from cache)
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("Should generate consistent hash for same image")
    void shouldGenerateConsistentHash() {
        // Given
        String imageBase64 = "testimage123";

        // When
        String hash1 = llmService.generateImageHash(imageBase64);
        String hash2 = llmService.generateImageHash(imageBase64);

        // Then
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length()); // SHA-256 produces 64 hex chars
    }

    @Test
    @DisplayName("Should generate different hashes for different images")
    void shouldGenerateDifferentHashesForDifferentImages() {
        // Given
        String image1 = "image1data";
        String image2 = "image2data";

        // When
        String hash1 = llmService.generateImageHash(image1);
        String hash2 = llmService.generateImageHash(image2);

        // Then
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Should have cache manager configured")
    void shouldHaveCacheManagerConfigured() {
        assertNotNull(cacheManager);
        assertNotNull(cacheManager.getCache("llm-responses"));
    }
}
