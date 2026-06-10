package com.example.goodsprice.llm.application.domain.service;

import static com.example.goodsprice.common.constant.ErrorMessageConstants.LLM_NOT_AVAILABLE_MSG;

import com.example.goodsprice.llm.application.port.in.LlmInPort;
import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService implements LlmInPort {

  private final LlmProviderPort llmProvider;

  private static final String LLM_RESPONSE_CACHE = "llm-responses";

  @Override
  @Cacheable(
      value = LLM_RESPONSE_CACHE,
      key = "#root.target.generateImageHash(#imageBase64)",
      unless = "#result == null || #result.isEmpty()")
  public Map<String, Object> extractReceipt(String imageBase64) {
    log.info(
        "Extracting receipt data using {} provider (cache miss - processing image)",
        llmProvider.getProviderName());

    if (!llmProvider.isAvailable()) {
      throw new IllegalStateException(
          LLM_NOT_AVAILABLE_MSG.formatted(llmProvider.getProviderName()));
    }

    return llmProvider.extractReceiptData(imageBase64);
  }

  public String generateImageHash(String imageBase64) {
    try {
      var digest = MessageDigest.getInstance("SHA-256");
      var hash = digest.digest(imageBase64.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      log.warn("Failed to generate image hash, using fallback", e);
      return String.valueOf(imageBase64.hashCode());
    }
  }

  @Override
  public String getCurrentProvider() {
    return llmProvider.getProviderName();
  }

  @Override
  public boolean isAvailable() {
    return llmProvider.isAvailable();
  }

  public void clearCache() {
    log.info("Clearing LLM response cache");
  }
}
