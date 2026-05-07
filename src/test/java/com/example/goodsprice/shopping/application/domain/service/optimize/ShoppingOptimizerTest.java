package com.example.goodsprice.shopping.application.domain.service.optimize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.shopping.application.domain.model.ShoppingOptimizationResult;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingOptimizerTest {

  @Mock private ProductInPort productInPort;
  @Mock private PriceInPort priceInPort;
  @Mock private StoreRepositoryPort storeRepository;

  @InjectMocks private ShoppingOptimizer optimizer;

  private ProductDomain apple;
  private ProductDomain bread;
  private StoreDomain storeA;
  private StoreDomain storeB;

  @BeforeEach
  void setUp() {
    apple = ProductDomain.builder().id(1L).name("Apple").unit("KG").build();
    bread = ProductDomain.builder().id(2L).name("Bread").unit("PIECE").build();
    storeA = StoreDomain.builder().id(10L).name("Store A").location("Jakarta").build();
    storeB = StoreDomain.builder().id(20L).name("Store B").location("Bandung").build();
  }

  @Test
  void shouldReturnEmptyResultForNullInput() {
    ShoppingOptimizationResult result = optimizer.optimize(null);
    assertNotNull(result);
    assertEquals(0, result.getTotalItems());
    assertEquals(0.0, result.getTotalCost());
    assertTrue(result.getRoute().isEmpty());
  }

  @Test
  void shouldReturnEmptyResultForEmptyInput() {
    ShoppingOptimizationResult result = optimizer.optimize(List.of());
    assertNotNull(result);
    assertEquals(0, result.getTotalItems());
    assertEquals(0.0, result.getTotalCost());
    assertTrue(result.getRoute().isEmpty());
  }

  @Test
  void shouldSelectCheapestByUnitPriceForWeightBasedProducts() {
    when(productInPort.findAllByNames(List.of("Apple"))).thenReturn(List.of(apple));
    PriceDomain priceA =
        PriceDomain.builder().id(1L).productId(1L).storeId(10L).price(2.0).unitPrice(4.0).build();
    PriceDomain priceB =
        PriceDomain.builder().id(2L).productId(1L).storeId(20L).price(3.5).unitPrice(3.5).build();
    when(priceInPort.findAllByProductIds(List.of(1L))).thenReturn(List.of(priceA, priceB));
    when(storeRepository.findAllById(List.of(20L))).thenReturn(List.of(storeB));

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Apple"));

    assertEquals(1, result.getTotalItems());
    assertEquals(1, result.getStoresToVisit());
    assertEquals(3.5, result.getTotalCost(), 0.001);

    var visit = result.getRoute().get(0);
    assertEquals(20L, visit.getStoreId());
    assertEquals(1, visit.getItems().size());
    assertEquals(3.5, visit.getItems().get(0).getPrice(), 0.001);
    assertEquals(3.5, visit.getSubtotal(), 0.001);
  }

  @Test
  void shouldSelectCheapestByPriceForNonWeightProducts() {
    when(productInPort.findAllByNames(List.of("Bread"))).thenReturn(List.of(bread));
    PriceDomain priceA =
        PriceDomain.builder().id(1L).productId(2L).storeId(10L).price(5.0).unitPrice(5.0).build();
    PriceDomain priceB =
        PriceDomain.builder().id(2L).productId(2L).storeId(20L).price(4.5).unitPrice(4.5).build();
    when(priceInPort.findAllByProductIds(List.of(2L))).thenReturn(List.of(priceA, priceB));
    when(storeRepository.findAllById(List.of(20L))).thenReturn(List.of(storeB));

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Bread"));

    assertEquals(1, result.getTotalItems());
    assertEquals(4.5, result.getTotalCost(), 0.001);

    var visit = result.getRoute().get(0);
    assertEquals(20L, visit.getStoreId());
    assertEquals(4.5, visit.getItems().get(0).getPrice(), 0.001);
  }

  @Test
  void shouldExcludeWeightProductWhenUnitPriceIsNullOrZero() {
    when(productInPort.findAllByNames(List.of("Apple"))).thenReturn(List.of(apple));
    PriceDomain priceWithNullUnitPrice =
        PriceDomain.builder().id(1L).productId(1L).storeId(10L).price(2.0).unitPrice(null).build();
    PriceDomain priceWithZeroUnitPrice =
        PriceDomain.builder().id(2L).productId(1L).storeId(20L).price(3.0).unitPrice(0.0).build();
    when(priceInPort.findAllByProductIds(List.of(1L)))
        .thenReturn(List.of(priceWithNullUnitPrice, priceWithZeroUnitPrice));

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Apple"));

    assertEquals(0, result.getTotalItems());
    assertEquals(0.0, result.getTotalCost());
    assertTrue(result.getRoute().isEmpty());
  }

  @Test
  void shouldExcludeNonWeightProductWhenPriceIsNullOrZero() {
    when(productInPort.findAllByNames(List.of("Bread"))).thenReturn(List.of(bread));
    PriceDomain priceWithNullPrice =
        PriceDomain.builder().id(1L).productId(2L).storeId(10L).price(null).unitPrice(1.0).build();
    PriceDomain priceWithZeroPrice =
        PriceDomain.builder().id(2L).productId(2L).storeId(20L).price(0.0).unitPrice(1.0).build();
    when(priceInPort.findAllByProductIds(List.of(2L)))
        .thenReturn(List.of(priceWithNullPrice, priceWithZeroPrice));

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Bread"));

    assertEquals(0, result.getTotalItems());
    assertEquals(0.0, result.getTotalCost());
    assertTrue(result.getRoute().isEmpty());
  }

  @Test
  void shouldHandleMixedWeightAndNonWeightProducts() {
    when(productInPort.findAllByNames(List.of("Apple", "Bread"))).thenReturn(List.of(apple, bread));
    PriceDomain applePriceA =
        PriceDomain.builder().id(1L).productId(1L).storeId(10L).price(6.0).unitPrice(3.0).build();
    PriceDomain applePriceB =
        PriceDomain.builder().id(2L).productId(1L).storeId(20L).price(4.0).unitPrice(4.0).build();
    PriceDomain breadPriceA =
        PriceDomain.builder().id(3L).productId(2L).storeId(10L).price(5.0).unitPrice(5.0).build();
    PriceDomain breadPriceB =
        PriceDomain.builder().id(4L).productId(2L).storeId(20L).price(4.0).unitPrice(4.0).build();
    when(priceInPort.findAllByProductIds(List.of(1L, 2L)))
        .thenReturn(List.of(applePriceA, applePriceB, breadPriceA, breadPriceB));
    when(storeRepository.findAllById(List.of(10L, 20L))).thenReturn(List.of(storeA, storeB));

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Apple", "Bread"));

    assertEquals(2, result.getTotalItems());
    assertEquals(2, result.getStoresToVisit());
    assertEquals(7.0, result.getTotalCost(), 0.001);

    var appleVisit =
        result.getRoute().stream()
            .filter(v -> v.getStoreId().equals(10L))
            .findFirst()
            .orElseThrow();
    assertEquals(3.0, appleVisit.getItems().get(0).getPrice(), 0.001);
    assertEquals(3.0, appleVisit.getSubtotal(), 0.001);

    var breadVisit =
        result.getRoute().stream()
            .filter(v -> v.getStoreId().equals(20L))
            .findFirst()
            .orElseThrow();
    assertEquals(4.0, breadVisit.getItems().get(0).getPrice(), 0.001);
    assertEquals(4.0, breadVisit.getSubtotal(), 0.001);
  }

  @Test
  void shouldGroupMultipleItemsFromSameStoreIntoSingleVisit() {
    ProductDomain banana = ProductDomain.builder().id(3L).name("Banana").unit("KG").build();
    when(productInPort.findAllByNames(List.of("Apple", "Banana")))
        .thenReturn(List.of(apple, banana));
    PriceDomain applePrice =
        PriceDomain.builder().id(1L).productId(1L).storeId(10L).price(3.0).unitPrice(3.0).build();
    PriceDomain bananaPrice =
        PriceDomain.builder().id(2L).productId(3L).storeId(10L).price(2.0).unitPrice(2.0).build();
    when(priceInPort.findAllByProductIds(List.of(1L, 3L)))
        .thenReturn(List.of(applePrice, bananaPrice));
    when(storeRepository.findAllById(List.of(10L))).thenReturn(List.of(storeA));

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Apple", "Banana"));

    assertEquals(2, result.getTotalItems());
    assertEquals(1, result.getStoresToVisit());
    assertEquals(5.0, result.getTotalCost(), 0.001);

    var visit = result.getRoute().get(0);
    assertEquals(10L, visit.getStoreId());
    assertEquals(2, visit.getItems().size());
    assertEquals(5.0, visit.getSubtotal(), 0.001);
  }

  @Test
  void shouldExcludeProductWhenStoreIsNotFound() {
    when(productInPort.findAllByNames(List.of("Apple"))).thenReturn(List.of(apple));
    PriceDomain price =
        PriceDomain.builder().id(1L).productId(1L).storeId(99L).price(3.0).unitPrice(3.0).build();
    when(priceInPort.findAllByProductIds(List.of(1L))).thenReturn(List.of(price));
    when(storeRepository.findAllById(List.of(99L))).thenReturn(List.of());

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Apple"));

    assertEquals(0, result.getTotalItems());
    assertTrue(result.getRoute().isEmpty());
  }

  @Test
  void shouldExcludeProductWhenNoPricesExist() {
    when(productInPort.findAllByNames(List.of("Apple"))).thenReturn(List.of(apple));
    when(priceInPort.findAllByProductIds(List.of(1L))).thenReturn(List.of());

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Apple"));

    assertEquals(0, result.getTotalItems());
    assertTrue(result.getRoute().isEmpty());
  }

  @Test
  void shouldHandleProductNotFoundInCatalog() {
    when(productInPort.findAllByNames(List.of("Unknown"))).thenReturn(List.of());

    ShoppingOptimizationResult result = optimizer.optimize(List.of("Unknown"));

    assertEquals(0, result.getTotalItems());
    assertTrue(result.getRoute().isEmpty());
  }
}
