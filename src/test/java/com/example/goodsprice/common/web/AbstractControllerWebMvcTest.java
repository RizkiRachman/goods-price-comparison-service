package com.example.goodsprice.common.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Abstract base test class for WebMvcTest files using MockMvc.
 *
 * <p>Provides common setUp() infrastructure and utility methods. Subclasses must implement abstract
 * hooks to provide their specific controller and adapter.
 *
 * <p>Inherited methods:
 *
 * <ul>
 *   <li>{@link #setUp()} — Configures ObjectMapper and MockMvc with controller and
 *       GlobalExceptionHandler
 *   <li>{@link #toJson(Object)} — Serializes objects to JSON
 * </ul>
 */
public abstract class AbstractControllerWebMvcTest {

  protected MockMvc mockMvc;
  protected ObjectMapper objectMapper;

  /**
   * Get the controller instance to test. Called during setUp() to configure MockMvc.
   *
   * @return the controller instance
   */
  protected abstract Object getController();

  @BeforeEach
  void setUp() {
    objectMapper =
        Jackson2ObjectMapperBuilder.json()
            .modules(new JsonNullableModule(), new JavaTimeModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    mockMvc =
        MockMvcBuilders.standaloneSetup(getController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
  }

  /**
   * Serialize an object to JSON string.
   *
   * @param obj the object to serialize
   * @return JSON string
   * @throws Exception if serialization fails
   */
  protected String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }
}
