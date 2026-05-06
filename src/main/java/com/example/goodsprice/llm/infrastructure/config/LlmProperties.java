package com.example.goodsprice.llm.infrastructure.config;

import com.example.goodsprice.llm.application.domain.model.LlmProviderType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

  private String provider;

  private ProviderConfig local = new ProviderConfig();
  private ProviderConfig openai = new ProviderConfig();
  private ProviderConfig anthropic = new ProviderConfig();
  private ProviderConfig gemini = new ProviderConfig();
  private ProviderConfig groq = new ProviderConfig();
  private ProviderConfig sumopod = new ProviderConfig();

  public ProviderConfig getActiveProvider() {
    return switch (LlmProviderType.fromValue(provider)) {
      case OPENAI -> openai;
      case ANTHROPIC -> anthropic;
      case GEMINI -> gemini;
      case GROQ -> groq;
      case SUMOPOD -> sumopod;
      default -> local;
    };
  }

  @Data
  public static class ProviderConfig {
    private String baseUrl;
    private String model;
    private int timeout = 30;
    private String apiKey;
    private boolean enabled = false;
    private String type = LlmConstants.TYPE_CLOUD;

    public boolean isLocal() {
      return LlmConstants.TYPE_LOCAL.equalsIgnoreCase(type);
    }

    public boolean isCloud() {
      return !isLocal();
    }
  }
}
