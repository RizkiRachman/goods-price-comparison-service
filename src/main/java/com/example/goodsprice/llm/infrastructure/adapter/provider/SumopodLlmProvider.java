package com.example.goodsprice.llm.infrastructure.adapter.provider;

import static com.example.goodsprice.llm.infrastructure.config.LlmConstants.PROVIDER_SUMOPOD;

import com.example.goodsprice.llm.infrastructure.config.LlmProperties;
import com.example.goodsprice.llm.infrastructure.config.LlmProperties.ProviderConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SumopodLlmProvider extends AbstractRestLlmProvider {

  private static final String API_URL = "https://ai.sumopod.com/v1/chat/completions";

  public SumopodLlmProvider(LlmProperties llmProperties, RestTemplate restTemplate) {
    super(llmProperties, restTemplate);
  }

  @Override
  protected String getApiUrl() {
    return API_URL;
  }

  @Override
  protected String getProviderNameConstant() {
    return PROVIDER_SUMOPOD;
  }

  @Override
  protected ProviderConfig getConfig() {
    return llmProperties.getSumopod();
  }
}
