package com.example.goodsprice.shopping.application.domain.service;

import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.shopping.application.domain.model.ShoppingItemDomain;
import com.example.goodsprice.shopping.application.domain.model.ShoppingOptimizationResult;
import com.example.goodsprice.shopping.application.domain.model.ShoppingSavingsDomain;
import com.example.goodsprice.shopping.application.domain.model.StoreVisitDomain;
import com.example.goodsprice.shopping.application.port.in.ShoppingInPort;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingService implements ShoppingInPort {

  private final PriceInPort priceInPort;
  private final StoreRepositoryPort storeRepository;
  private final ProductInPort productInPort;

  @Override
  public ShoppingOptimizationResult optimizeShoppingRoute(List<String> itemNames) {
    if (Objects.isNull(itemNames) || itemNames.isEmpty()) {
      return ShoppingOptimizationResult.builder()
          .totalItems(0)
          .totalCost(0.0)
          .storesToVisit(0)
          .route(List.of())
          .savings(
              ShoppingSavingsDomain.builder().comparedToSingleStore(0.0).percentage(0.0).build())
          .build();
    }

    Map<Long, StoreAccumulator> storeAccumulators = new LinkedHashMap<>();
    double totalCost = 0.0;
    int productCount = 0;

    for (String itemName : itemNames) {
      var product = productInPort.findByName(itemName);
      if (Objects.isNull(product)) {
        log.warn("Product not found: {}", itemName);
        continue;
      }

      var cheapest = priceInPort.findCheapestByProduct(product.getId());
      if (Objects.isNull(cheapest)) {
        log.warn("No price found for product: {}", itemName);
        continue;
      }

      var store = storeRepository.findById(cheapest.getStoreId());
      if (Objects.isNull(store)) {
        log.warn("Store not found for id: {}", cheapest.getStoreId());
        continue;
      }

      var item =
          ShoppingItemDomain.builder()
              .productName(product.getName())
              .price(cheapest.getPrice())
              .quantity(1.0)
              .build();

      var accumulator = storeAccumulators.get(store.getId());
      if (Objects.isNull(accumulator)) {
        accumulator = new StoreAccumulator(store.getId(), store.getName(), store.getLocation());
        storeAccumulators.put(store.getId(), accumulator);
      }
      accumulator.items.add(item);
      accumulator.subtotal += cheapest.getPrice();

      totalCost += cheapest.getPrice();
      productCount++;
    }

    var route =
        storeAccumulators.values().stream()
            .map(
                a ->
                    StoreVisitDomain.builder()
                        .storeId(a.storeId)
                        .storeName(a.storeName)
                        .storeLocation(a.storeLocation)
                        .items(List.copyOf(a.items))
                        .subtotal(a.subtotal)
                        .estimatedTime("15 min")
                        .build())
            .toList();

    var savings =
        ShoppingSavingsDomain.builder().comparedToSingleStore(0.0).percentage(0.0).build();

    log.info(
        "Shopping optimization: {} items across {} stores, total cost: {}",
        productCount,
        route.size(),
        totalCost);

    return ShoppingOptimizationResult.builder()
        .totalItems(productCount)
        .totalCost(totalCost)
        .storesToVisit(route.size())
        .route(route)
        .savings(savings)
        .build();
  }

  private static class StoreAccumulator {

    final Long storeId;
    final String storeName;
    final String storeLocation;
    final List<ShoppingItemDomain> items;
    double subtotal;

    StoreAccumulator(Long storeId, String storeName, String storeLocation) {
      this.storeId = storeId;
      this.storeName = storeName;
      this.storeLocation = storeLocation;
      this.items = new ArrayList<>();
      this.subtotal = 0.0;
    }
  }
}
