package com.example.goodsprice.shopping.application.domain.service.optimize;

import com.example.goodsprice.common.constant.UnitConstants;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
        .then(this::resolveAllPrices)
        .then(this::selectBestPrices)
        .then(this::resolveStores)
        .then(this::buildRoute)
        .then(this::toResult)
        .value();
  }

  private ShoppingContext resolveProducts(ShoppingContext ctx) {
    ctx.products = productInPort.findAllByNames(ctx.itemNames);

    ctx.productById = ctx.products.stream().collect(Collectors.toMap(ProductDomain::getId, p -> p));
    return ctx;
  }

  private ShoppingContext resolveAllPrices(ShoppingContext ctx) {
    if (ctx.products.isEmpty()) return ctx;

    List<Long> ids = ctx.products.stream().map(ProductDomain::getId).toList();

    List<PriceDomain> allPrices = priceInPort.findAllByProductIds(ids);
    ctx.allPricesByProductId =
        allPrices.stream().collect(Collectors.groupingBy(PriceDomain::getProductId));
    return ctx;
  }

  private ShoppingContext selectBestPrices(ShoppingContext ctx) {
    if (ctx.allPricesByProductId.isEmpty()) return ctx;

    ctx.bestPricesByProductId =
        ctx.allPricesByProductId.entrySet().stream()
            .filter(entry -> !entry.getValue().isEmpty())
            .flatMap(
                entry ->
                    findBestPrice(entry.getKey(), entry.getValue(), ctx).stream()
                        .map(best -> Map.entry(entry.getKey(), best)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return ctx;
  }

  private Optional<PriceDomain> findBestPrice(
      Long productId, List<PriceDomain> prices, ShoppingContext ctx) {
    ProductDomain product = ctx.productById.get(productId);
    if (Objects.isNull(product)) return Optional.empty();
    boolean isWeight = UnitConstants.isWeight(product.getUnit());
    return prices.stream()
        .filter(p -> isValidPriceValue(p, isWeight))
        .min(Comparator.comparingDouble(p -> effectivePriceValue(p, isWeight)));
  }

  private static boolean isValidPriceValue(PriceDomain price, boolean isWeight) {
    Double value = isWeight ? price.getUnitPrice() : price.getPrice();
    return Objects.nonNull(value) && value > 0;
  }

  private static double effectivePriceValue(PriceDomain price, boolean isWeight) {
    return isWeight ? price.getUnitPrice() : price.getPrice();
  }

  private ShoppingContext resolveStores(ShoppingContext ctx) {
    if (ctx.bestPricesByProductId.isEmpty()) return ctx;

    Set<Long> storeIdSet =
        ctx.bestPricesByProductId.values().stream()
            .filter(Objects::nonNull)
            .map(PriceDomain::getStoreId)
            .collect(Collectors.toCollection(LinkedHashSet::new));

    List<StoreDomain> stores = storeRepository.findAllById(new ArrayList<>(storeIdSet));
    ctx.storeById =
        stores.stream().collect(Collectors.toMap(StoreDomain::getId, storeDomain -> storeDomain));
    return ctx;
  }

  private ShoppingContext buildRoute(ShoppingContext ctx) {
    Map<Long, ProductDomain> validProducts = new ConcurrentHashMap<>();
    Map<Long, List<ProductDomain>> productsByStore = new ConcurrentHashMap<>();

    for (ProductDomain product : ctx.products) {
      PriceDomain bestPrice = ctx.bestPricesByProductId.get(product.getId());
      if (bestPrice == null) continue;
      if (!ctx.storeById.containsKey(bestPrice.getStoreId())) continue;

      validProducts.put(product.getId(), product);
      productsByStore.computeIfAbsent(bestPrice.getStoreId(), k -> new ArrayList<>()).add(product);
    }
    ctx.validProducts = validProducts;

    List<StoreVisitDomain> route = new ArrayList<>();
    for (Map.Entry<Long, List<ProductDomain>> entry : productsByStore.entrySet()) {
      route.add(toStoreVisit(entry.getKey(), entry.getValue(), ctx));
    }
    ctx.route = route;
    return ctx;
  }

  private StoreVisitDomain toStoreVisit(
      Long storeId, List<ProductDomain> products, ShoppingContext ctx) {
    StoreDomain store = ctx.storeById.get(storeId);
    List<ShoppingItemDomain> items = new ArrayList<>(products.size());
    double subtotal = 0.0;

    for (ProductDomain product : products) {
      PriceDomain price = ctx.bestPricesByProductId.get(product.getId());
      double effectivePrice = getEffectivePrice(price, product);
      items.add(
          ShoppingItemDomain.builder()
              .productName(product.getName())
              .price(effectivePrice)
              .unit(product.getUnit())
              .build());
      subtotal += effectivePrice;
    }

    return StoreVisitDomain.builder()
        .storeId(store.getId())
        .storeName(store.getName())
        .storeLocation(store.getLocation())
        .items(items)
        .subtotal(subtotal)
        .build();
  }

  private ShoppingOptimizationResult toResult(ShoppingContext ctx) {
    double totalCost = 0.0;
    for (ProductDomain product : ctx.validProducts.values()) {
      PriceDomain price = ctx.bestPricesByProductId.get(product.getId());
      totalCost += getEffectivePrice(price, product);
    }

    log.info(
        "Shopping optimization: {} items across {} stores, total cost: {}",
        ctx.validProducts.size(),
        ctx.route.size(),
        totalCost);
    return ShoppingOptimizationResult.builder()
        .totalItems(ctx.validProducts.size())
        .totalCost(totalCost)
        .storesToVisit(ctx.route.size())
        .route(ctx.route)
        .savings(ShoppingSavingsDomain.builder().comparedToSingleStore(0.0).percentage(0.0).build())
        .build();
  }

  private double getEffectivePrice(PriceDomain price, ProductDomain product) {
    if (Objects.isNull(price)) return 0.0;
    return UnitConstants.isWeight(product.getUnit()) ? price.getUnitPrice() : price.getPrice();
  }

  private ShoppingOptimizationResult emptyResult() {
    return ShoppingOptimizationResult.builder()
        .totalItems(0)
        .totalCost(0.0)
        .storesToVisit(0)
        .route(List.of())
        .savings(ShoppingSavingsDomain.builder().comparedToSingleStore(0.0).percentage(0.0).build())
        .build();
  }
}
