package com.example.goodsprice.shopping.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.ShoppingOptimizeRequest;
import com.example.goodsprice.api.model.ShoppingOptimizeResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ShoppingControllerTest {

  @Mock private ShoppingWebAdapter adapter;

  @InjectMocks private ShoppingController controller;

  @Test
  @DisplayName("Should optimize shopping route")
  void shouldOptimize() {
    var request = new ShoppingOptimizeRequest();
    request.setItems(List.of("Apple", "Bread"));

    var response = new ShoppingOptimizeResponse();
    response.setTotalCost(10.0);
    when(adapter.optimize(any(ShoppingOptimizeRequest.class))).thenReturn(response);

    var result = controller.optimizeShoppingRoute(request);

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(10.0, result.getBody().getTotalCost());
    verify(adapter).optimize(request);
  }
}
