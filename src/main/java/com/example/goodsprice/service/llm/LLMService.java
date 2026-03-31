package com.example.goodsprice.service.llm;

import com.example.goodsprice.config.CacheConfiguration;
import com.example.goodsprice.config.properties.LlmProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Service for LLM operations with caching support.
 * Delegates to the configured LLM provider and caches responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {

    private final LLMProvider llmProvider;
    private final LlmProperties llmProperties;

    /**
     * Extract receipt data from image using configured LLM.
     * Results are cached based on image content hash to avoid reprocessing
     * the same images.
     *
     * @param imageBase64 Base64 encoded image
     * @return Extracted receipt data
     */
    @Cacheable(
        value = CacheConfiguration.LLM_RESPONSE_CACHE,
        key = "#root.target.generateImageHash(#imageBase64)",
        unless = "#result == null || #result.isEmpty()"
    )
    public Map<String, Object> extractReceipt(String imageBase64) {
        log.info("Extracting receipt data using {} provider (cache miss - processing image)",
            llmProvider.getProviderName());

        if (!llmProvider.isAvailable()) {
            throw new IllegalStateException("LLM provider is not available: " + llmProvider.getProviderName());
        }

        return llmProvider.extractReceiptData(imageBase64);
    }

    /**
     * Generate a hash of the image for use as cache key.
     * This ensures identical images get the same cache key.
     *
     * @param imageBase64 Base64 encoded image
     * @return SHA-256 hash of the image
     */
    public String generateImageHash(String imageBase64) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(imageBase64.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.warn("Failed to generate image hash, using fallback", e);
            return String.valueOf(imageBase64.hashCode());
        }
    }

    /**
     * Get current active provider name.
     *
     * @return Provider name
     */
    public String getCurrentProvider() {
        return llmProperties.getProvider();
    }

    /**
     * Check if the LLM provider is available.
     *
     * @return true if provider is ready
     */
    public boolean isAvailable() {
        return llmProvider.isAvailable();
    }

    /**
     * Clear the LLM response cache.
     * Useful for testing or when models are updated.
     */
    public void clearCache() {
        log.info("Clearing LLM response cache");
        // Cache will be cleared automatically based on TTL
        // This method can be extended to manually clear if needed
    }
}
