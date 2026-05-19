package com.example.goodsprice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    var offsetDateTimeModule = new SimpleModule();
    offsetDateTimeModule.addDeserializer(
        OffsetDateTime.class, new LenientOffsetDateTimeDeserializer());

    var mapper = new ObjectMapper();
    mapper.registerModule(new JsonNullableModule());
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(offsetDateTimeModule);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }
}
