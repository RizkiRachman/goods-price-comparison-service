package com.example.goodsprice.shopping.application.domain.service.optimize;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.shopping.application.domain.model.ShoppingOptimizationResult;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ShoppingOptimizerCacheTest {

  @Autowired private ProductInPort productInPort;

  @Autowired private PriceInPort priceInPort;

  @Autowired private StoreRepositoryPort storeRepository;

  @Autowired private ShoppingOptimizer shoppingOptimizer;

  @Autowired private CacheManager cacheManager;

  @BeforeEach
  void setUp() {
    when(productInPort.findAllByNames(anyList())).thenReturn(List.of());
    when(priceInPort.findAllByProductIds(anyList())).thenReturn(List.of());
    when(storeRepository.findAllById(anyList())).thenReturn(List.of());
  }

  @Test
  @DisplayName("Should return cached result for same item names")
  void shouldCacheOptimizationResultForSameItemNames() {
    List<String> itemNames = List.of("Apple", "Bread");

    ShoppingOptimizationResult result1 = shoppingOptimizer.optimize(itemNames);
    ShoppingOptimizationResult result2 = shoppingOptimizer.optimize(itemNames);

    assertNotNull(result1);
    assertNotNull(result2);
    assertSame(result1, result2, "Second call should return the same cached instance");
    verify(productInPort, times(1)).findAllByNames(itemNames);
  }

  @Test
  @DisplayName("Should produce different cache entries for different item names")
  void shouldNotUseCacheForDifferentItemNames() {
    List<String> itemNames1 = List.of("Apple");
    List<String> itemNames2 = List.of("Bread");

    ShoppingOptimizationResult result1 = shoppingOptimizer.optimize(itemNames1);
    ShoppingOptimizationResult result2 = shoppingOptimizer.optimize(itemNames2);

    assertNotNull(result1);
    assertNotNull(result2);
    verify(productInPort, times(1)).findAllByNames(itemNames1);
    verify(productInPort, times(1)).findAllByNames(itemNames2);
  }

  @Test
  @DisplayName("Should have shopping-optimization cache configured in CacheManager")
  void shouldHaveShoppingOptimizationCacheConfigured() {
    assertNotNull(cacheManager);
    assertNotNull(cacheManager.getCache("shopping-optimization"));
  }

  @TestConfiguration
  static class TestMockConfiguration {

    @Bean
    @Primary
    ProductInPort productInPort() {
      return mock(ProductInPort.class);
    }

    @Bean
    @Primary
    PriceInPort priceInPort() {
      return mock(PriceInPort.class);
    }

    @Bean
    @Primary
    StoreRepositoryPort storeRepository() {
      return mock(StoreRepositoryPort.class);
    }
  }
}
