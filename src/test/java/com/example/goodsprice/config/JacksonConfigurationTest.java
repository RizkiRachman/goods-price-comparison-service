package com.example.goodsprice.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

class JacksonConfigurationTest {

  private final JacksonConfiguration config = new JacksonConfiguration();

  @Test
  void shouldCreateObjectMapperWithModules() {
    var mapper = config.objectMapper();

    assertNotNull(mapper);
    assertFalse(mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
  }

  @Test
  void shouldRegisterJsonNullableModule() {
    var mapper = config.objectMapper();

    assertNotNull(mapper.getRegisteredModuleIds());
  }

  @Test
  void shouldRegisterJavaTimeModule() {
    var mapper = config.objectMapper();

    assertNotNull(mapper.findAndRegisterModules());
  }
}
