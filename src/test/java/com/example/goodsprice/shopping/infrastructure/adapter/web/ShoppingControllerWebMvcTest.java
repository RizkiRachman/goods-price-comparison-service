package com.example.goodsprice.shopping.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.ShoppingOptimizeRequest;
import com.example.goodsprice.api.model.ShoppingOptimizeResponse;
import com.example.goodsprice.common.web.AbstractControllerWebMvcTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class ShoppingControllerWebMvcTest extends AbstractControllerWebMvcTest {

  @Mock private ShoppingWebAdapter adapter;

  @Override
  protected Object getController() {
    return new ShoppingController(adapter);
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
