package com.example.goodsprice.service.receipt;

import com.example.goodsprice.service.llm.LLMProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * Service for receipt processing and OCR extraction.
 * Integrates directly with LLM provider (Gemini) for image analysis.
 * Implements image deduplication using SHA-256 hashing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final LLMProvider llmProvider;

    /**
     * Process receipt image and extract structured data.
     * Uses image hash deduplication to avoid processing the same image twice.
     *
     * @param image MultipartFile containing receipt image
     * @return Extracted receipt data
     */
    public Map<String, Object> processReceipt(MultipartFile image) {
        log.info("Processing receipt image: {}", image.getOriginalFilename());

        try {
            // Get image bytes and calculate hash for deduplication
            byte[] imageBytes = image.getBytes();
            String imageHash = calculateSha256Hash(imageBytes);
            
            // Check if we've already processed this exact image
            return processReceiptWithCache(imageHash, imageBytes, image.getOriginalFilename());

        } catch (Exception e) {
            log.error("Failed to process receipt: {}", e.getMessage(), e);
            throw new RuntimeException("Receipt processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Process receipt with caching based on image hash.
     * If the same image is uploaded again, return cached result.
     */
    @Cacheable(value = "receipt-processing", key = "#imageHash", unless = "#result == null")
    public Map<String, Object> processReceiptWithCache(String imageHash, byte[] imageBytes, String originalFilename) {
        log.info("Processing receipt with hash: {} (cache miss)", imageHash);
        
        // Convert to base64 for LLM processing
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        // Extract data using LLM (Gemini)
        Map<String, Object> extractedData = llmProvider.extractReceiptData(base64Image);

        // Add metadata
        extractedData.put("receiptId", UUID.randomUUID().toString());
        extractedData.put("status", "COMPLETED");
        extractedData.put("originalFilename", originalFilename);
        extractedData.put("imageHash", imageHash); // Store hash for reference

        log.info("Receipt processed successfully: {} (hash: {})", 
                extractedData.get("receiptId"), imageHash);

        return extractedData;
    }

    /**
     * Calculate SHA-256 hash of image bytes for deduplication.
     * Same image = same hash = cached result.
     */
    private String calculateSha256Hash(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available, this shouldn't happen
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
