package com.example.goodsprice.shopping.application.domain.service.optimize;

import com.example.goodsprice.common.util.Pipeline;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.shopping.application.domain.model.ShoppingItemDomain;
import com.example.goodsprice.shopping.application.domain.model.ShoppingOptimizationResult;
import com.example.goodsprice.shopping.application.domain.model.ShoppingSavingsDomain;
import com.example.goodsprice.shopping.application.domain.model.StoreVisitDomain;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShoppingOptimizer {

  private final ProductInPort productInPort;
  private final PriceInPort priceInPort;
  private final StoreRepositoryPort storeRepository;

  public ShoppingOptimizationResult optimize(List<String> itemNames) {
    if (Objects.isNull(itemNames) || itemNames.isEmpty()) {
      return emptyResult();
    }
    return Pipeline.of(new ShoppingContext(itemNames))
        .then(this::resolveProducts)
        .then(this::resolvePrices)
        .then(this::resolveStores)
        .then(this::buildRoute)
        .then(this::toResult)
        .value();
  }

  private ShoppingContext resolveProducts(ShoppingContext ctx) {
    ctx.products = productInPort.findAllByNames(ctx.itemNames);
    Set<String> found = ctx.products.stream().map(ProductDomain::getName).collect(Collectors.toSet());
    ctx.itemNames.stream()
        .filter(name -> !found.contains(name))
        .forEach(name -> log.warn("Product not found: {}", name));
    return ctx;
  }

  private ShoppingContext resolvePrices(ShoppingContext ctx) {
    if (ctx.products.isEmpty()) return ctx;
    var ids = ctx.products.stream().map(ProductDomain::getId).toList();
    ctx.cheapestByProductId = priceInPort.findCheapestByProducts(ids);
    ctx.products.stream()
        .filter(p -> !ctx.cheapestByProductId.containsKey(p.getId()))
        .forEach(p -> log.warn("No price found for product: {}", p.getName()));
    return ctx;
  }

  private ShoppingContext resolveStores(ShoppingContext ctx) {
    if (ctx.cheapestByProductId.isEmpty()) return ctx;
    var storeIds = ctx.cheapestByProductId.values().stream()
        .map(PriceDomain::getStoreId).distinct().toList();
    ctx.storeById = storeRepository.findAllById(storeIds).stream()
        .collect(Collectors.toMap(StoreDomain::getId, Function.identity()));
    ctx.cheapestByProductId.values().stream()
        .filter(price -> !ctx.storeById.containsKey(price.getStoreId()))
        .forEach(price -> log.warn("Store not found for id: {}", price.getStoreId()));
    return ctx;
  }

  private ShoppingContext buildRoute(ShoppingContext ctx) {
    ctx.validProducts = ctx.products.stream()
        .filter(p -> ctx.cheapestByProductId.containsKey(p.getId()))
        .filter(p -> ctx.storeById.containsKey(ctx.cheapestByProductId.get(p.getId()).getStoreId()))
        .toList();
    ctx.route = ctx.validProducts.stream()
        .collect(Collectors.groupingBy(
            p -> ctx.cheapestByProductId.get(p.getId()).getStoreId(),
            LinkedHashMap::new,
            Collectors.toList()))
        .entrySet().stream()
        .map(entry -> toStoreVisit(entry.getKey(), entry.getValue(), ctx))
        .toList();
    return ctx;
  }

  private StoreVisitDomain toStoreVisit(Long storeId, List<ProductDomain> products, ShoppingContext ctx) {
    var store = ctx.storeById.get(storeId);
    var items = products.stream()
        .map(p -> ShoppingItemDomain.builder()
            .productName(p.getName())
            .price(ctx.cheapestByProductId.get(p.getId()).getPrice())
            .quantity(1.0)
            .build())
        .toList();
    var subtotal = products.stream()
        .mapToDouble(p -> ctx.cheapestByProductId.get(p.getId()).getPrice())
        .sum();
    return StoreVisitDomain.builder()
        .storeId(store.getId())
        .storeName(store.getName())
        .storeLocation(store.getLocation())
        .items(items)
        .subtotal(subtotal)
        .estimatedTime("15 min")
        .build();
  }

  private ShoppingOptimizationResult toResult(ShoppingContext ctx) {
    var totalCost = ctx.validProducts.stream()
        .mapToDouble(p -> ctx.cheapestByProductId.get(p.getId()).getPrice())
        .sum();
    log.info("Shopping optimization: {} items across {} stores, total cost: {}",
        ctx.validProducts.size(), ctx.route.size(), totalCost);
    return ShoppingOptimizationResult.builder()
        .totalItems(ctx.validProducts.size())
        .totalCost(totalCost)
        .storesToVisit(ctx.route.size())
        .route(ctx.route)
        .savings(ShoppingSavingsDomain.builder().comparedToSingleStore(0.0).percentage(0.0).build())
        .build();
  }

  private ShoppingOptimizationResult emptyResult() {
    return ShoppingOptimizationResult.builder()
        .totalItems(0).totalCost(0.0).storesToVisit(0).route(List.of())
        .savings(ShoppingSavingsDomain.builder().comparedToSingleStore(0.0).percentage(0.0).build())
        .build();
  }
}
