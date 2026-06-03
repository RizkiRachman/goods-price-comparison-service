package com.example.goodsprice.shopping.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.goodsprice.shopping.application.domain.model.ShoppingOptimizationResult;
import com.example.goodsprice.shopping.application.domain.service.optimize.ShoppingOptimizer;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

  @Mock private ShoppingOptimizer optimizer;

  @InjectMocks private ShoppingService shoppingService;

  @Test
  void optimizeShoppingRoute_shouldCallOptimizerWithCorrectItems() {
    // Given
    List<String> itemNames = Arrays.asList("apple", "banana", "orange");
    ShoppingOptimizationResult expectedResult = mock(ShoppingOptimizationResult.class);

    // Mock the optimizer behavior
    when(optimizer.optimize(itemNames)).thenReturn(expectedResult);

    // When
    ShoppingOptimizationResult actualResult = shoppingService.optimizeShoppingRoute(itemNames);

    // Then
    // Verify that the optimizer's optimize method was called exactly once with the correct
    // arguments
    verify(optimizer, times(1)).optimize(itemNames);
    // Verify that the result returned by the service is the same as the result from the optimizer
    assertEquals(expectedResult, actualResult);
  }
}
