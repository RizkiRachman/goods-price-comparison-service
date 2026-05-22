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
import org.springframework.web.client.RestTemplate;

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
  public RestTemplate restTemplate() {
    var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(10_000);
    factory.setReadTimeout(30_000);
    return new RestTemplate(factory);
  }

  @Bean
  public GroqLlmProvider groqLlmProvider(LlmProperties llmProperties, RestTemplate restTemplate) {
    return new GroqLlmProvider(llmProperties, restTemplate);
  }

  @Bean
  public SumopodLlmProvider sumopodLlmProvider(
      LlmProperties llmProperties, RestTemplate restTemplate) {
    return new SumopodLlmProvider(llmProperties, restTemplate);
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
