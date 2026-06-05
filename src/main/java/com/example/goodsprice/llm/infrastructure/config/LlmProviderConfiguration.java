package com.example.goodsprice.llm.infrastructure.config;

import com.example.goodsprice.llm.application.domain.model.LlmProviderType;
import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import com.example.goodsprice.llm.infrastructure.adapter.provider.GeminiLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.GroqLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.LocalLlmProvider;
import com.example.goodsprice.llm.infrastructure.adapter.provider.SumopodLlmProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
    var cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(20);
    cm.setDefaultMaxPerRoute(10);
    var httpClient = HttpClients.custom().setConnectionManager(cm).build();
    var factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(factory);
  }

  @Bean
  public GroqLlmProvider groqLlmProvider(
      LlmProperties llmProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
    return new GroqLlmProvider(llmProperties, restTemplate, objectMapper);
  }

  @Bean
  public SumopodLlmProvider sumopodLlmProvider(
      LlmProperties llmProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
    return new SumopodLlmProvider(llmProperties, restTemplate, objectMapper);
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
        if (type == LlmProviderType.LOCAL && Objects.nonNull(llmProperties.getProvider())) {
          log.warn("Unknown provider '{}', falling back to local", llmProperties.getProvider());
        }
        yield localProvider;
      }
    };
  }
}
