package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.PROVIDER_GROQ;

import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import com.example.goodsprice.llm.infrastructure.config.LlmProperties.ProviderConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GroqLlmProvider extends AbstractRestLlmProvider {

  private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

  public GroqLlmProvider(
      LlmProperties llmProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
    super(llmProperties, restTemplate, objectMapper);
  }

  @Override
  protected String getApiUrl() {
    return API_URL;
  }

  @Override
  protected String getProviderNameConstant() {
    return PROVIDER_GROQ;
  }

  @Override
  protected ProviderConfig getConfig() {
    return llmProperties.getGroq();
  }
}
