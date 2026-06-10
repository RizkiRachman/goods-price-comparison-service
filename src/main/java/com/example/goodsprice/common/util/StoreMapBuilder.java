package com.example.goodsprice.common.util;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class StoreMapBuilder {
  private StoreMapBuilder() {}

  public static Map<Long, StoreDomain> buildFromPrices(
      List<PriceDomain> prices, Function<List<Long>, List<StoreDomain>> storeFetcher) {
    var storeIds =
        prices.stream().map(PriceDomain::getStoreId).filter(Objects::nonNull).distinct().toList();
    if (storeIds.isEmpty()) return Map.of();
    return storeFetcher.apply(storeIds).stream()
        .collect(CollectorUtils.toIdentityMap(StoreDomain::getId));
  }
}
