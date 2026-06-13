package com.example.goodsprice.shopping.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.ShoppingOptimizeRequest;
import com.example.goodsprice.api.model.ShoppingOptimizeResponse;
import com.example.goodsprice.common.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ShoppingControllerWebMvcTest {

  @Mock private ShoppingWebAdapter adapter;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper =
        Jackson2ObjectMapperBuilder.json()
            .modules(new JsonNullableModule(), new JavaTimeModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    var controller = new ShoppingController(adapter);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
  }

  private String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }

  @Test
  @DisplayName("POST /v1/shopping/optimize should return 200 OK with optimized route")
  void shouldOptimizeReturns200() throws Exception {
    var response = new ShoppingOptimizeResponse();
    response.setTotalItems(2);
    response.setTotalCost(10.0);
    response.setStoresToVisit(1);
    when(adapter.optimize(any(ShoppingOptimizeRequest.class))).thenReturn(response);

    var request = new ShoppingOptimizeRequest();
    request.setItems(List.of("Apple", "Bread"));

    mockMvc
        .perform(
            post("/v1/shopping/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalItems").value(2))
        .andExpect(jsonPath("$.totalCost").value(10.0))
        .andExpect(jsonPath("$.storesToVisit").value(1));
  }

  @Test
  @DisplayName("POST /v1/shopping/optimize should return 200 OK with empty items list")
  void shouldOptimizeWithEmptyItemsReturns200() throws Exception {
    var response = new ShoppingOptimizeResponse();
    response.setTotalItems(0);
    response.setTotalCost(0.0);
    response.setStoresToVisit(0);
    when(adapter.optimize(any(ShoppingOptimizeRequest.class))).thenReturn(response);

    var request = new ShoppingOptimizeRequest();
    request.setItems(List.of());

    mockMvc
        .perform(
            post("/v1/shopping/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalItems").value(0))
        .andExpect(jsonPath("$.totalCost").value(0.0))
        .andExpect(jsonPath("$.storesToVisit").value(0));
  }

  @Test
  @DisplayName("POST /v1/shopping/optimize should return 400 when request body is invalid")
  void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/v1/shopping/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
        .andExpect(status().isBadRequest());
  }
}
