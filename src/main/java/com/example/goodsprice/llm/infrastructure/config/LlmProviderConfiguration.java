package com.example.goodsprice.llm.infrastructure.config;

import com.example.goodsprice.llm.application.domain.model.LlmProviderType;
import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import com.example.goodsprice.llm.infrastructure.adapter.provider.GeminiLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.GroqLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.LocalLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.SumopodLlmProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LlmProviderConfiguration {

  @Bean
  public GeminiLlmProvider geminiLlmProvider(LlmProperties llmProperties) {
    return new GeminiLlmProvider(llmProperties);
  }

  @Bean
  public LocalLlmProvider localLlmProvider(LlmProperties llmProperties) {
    return new LocalLlmProvider(llmProperties);
  }

  @Bean
  public GroqLlmProvider groqLlmProvider(LlmProperties llmProperties) {
    return new GroqLlmProvider(llmProperties);
  }

  @Bean
  public SumopodLlmProvider sumopodLlmProvider(LlmProperties llmProperties) {
    return new SumopodLlmProvider(llmProperties);
  }

  @Bean
  public LlmProviderPort llmProvider(
      LlmProperties llmProperties,
      GeminiLlmProvider geminiProvider,
      LocalLlmProvider localProvider,
      GroqLlmProvider groqProvider,
      SumopodLlmProvider sumopodProvider) {
    var type = LlmProviderType.fromValue(llmProperties.getProvider());
    log.info("Configuring LLM provider: {}", type);
    return switch (type) {
      case GEMINI -> geminiProvider;
      case GROQ -> groqProvider;
      case SUMOPOD -> sumopodProvider;
      default -> {
        if (type == LlmProviderType.LOCAL && llmProperties.getProvider() != null) {
          log.warn("Unknown provider '{}', falling back to local", llmProperties.getProvider());
        }
        yield localProvider;
      }
    };
  }
}
