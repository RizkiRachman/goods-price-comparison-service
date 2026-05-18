package com.example.goodsprice.config;

import com.example.goodsprice.api.model.CreateFeedbackQuestionRequest;
import com.example.goodsprice.api.model.FeedbackQuestion;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Locale;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {

  @Bean
  public Jackson2ObjectMapperBuilder objectMapperBuilder() {
    var module = new SimpleModule();
    module.addDeserializer(OffsetDateTime.class, new LenientOffsetDateTimeDeserializer());
    module.addDeserializer(
        CreateFeedbackQuestionRequest.TypeEnum.class,
        new CaseInsensitiveEnumDeserializer<>(CreateFeedbackQuestionRequest.TypeEnum.class));
    module.addDeserializer(
        FeedbackQuestion.TypeEnum.class,
        new CaseInsensitiveEnumDeserializer<>(FeedbackQuestion.TypeEnum.class));

    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder
        .modules(new JsonNullableModule(), new JavaTimeModule(), module)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return builder;
  }

  private static class CaseInsensitiveEnumDeserializer<T extends Enum<T>>
      extends JsonDeserializer<T> {

    private final Class<T> enumClass;

    CaseInsensitiveEnumDeserializer(Class<T> enumClass) {
      this.enumClass = enumClass;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      var value = p.getText().toUpperCase(Locale.ROOT);
      try {
        return Enum.valueOf(enumClass, value);
      } catch (IllegalArgumentException e) {
        throw ctxt.weirdStringException(value, enumClass, "Unknown enum value: " + p.getText())
            .withCause(e);
      }
    }
  }
}
