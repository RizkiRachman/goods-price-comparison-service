package com.example.goodsprice.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {

  @Bean
  public Jackson2ObjectMapperBuilder objectMapperBuilder() {
    var offsetDateTimeModule = new SimpleModule();
    offsetDateTimeModule.addDeserializer(
        OffsetDateTime.class, new LenientOffsetDateTimeDeserializer());

    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder
        .modules(new JsonNullableModule(), new JavaTimeModule(), offsetDateTimeModule)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return builder;
  }
}
