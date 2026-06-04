package com.example.goodsprice.shopping.application.domain.service.optimize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ShoppingContextTest {

  @Test
  void shouldInitializeWithItemNames() {
    var context = new ShoppingContext(List.of("Apple", "Bread"));

    assertNotNull(context);
    assertEquals(2, context.itemNames.size());
    assertTrue(context.itemNames.contains("Apple"));
    assertTrue(context.itemNames.contains("Bread"));
  }

  @Test
  void shouldInitializeWithEmptyLists() {
    var context = new ShoppingContext(List.of("Apple"));

    assertNotNull(context);
    assertTrue(context.products.isEmpty());
    assertTrue(context.productById.isEmpty());
    assertTrue(context.allPricesByProductId.isEmpty());
    assertTrue(context.bestPricesByProductId.isEmpty());
    assertTrue(context.storeById.isEmpty());
    assertTrue(context.validProducts.isEmpty());
    assertTrue(context.route.isEmpty());
  }

  @Test
  void shouldHandleEmptyItemNames() {
    var context = new ShoppingContext(List.of());

    assertNotNull(context);
    assertTrue(context.itemNames.isEmpty());
  }
}
