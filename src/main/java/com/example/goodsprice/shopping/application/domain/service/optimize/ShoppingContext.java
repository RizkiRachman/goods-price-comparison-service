package com.example.goodsprice.shopping.application.domain.service.optimize;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.shopping.application.domain.model.StoreVisitDomain;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import java.util.List;
import java.util.Map;

class ShoppingContext {

  final List<String> itemNames;
  List<ProductDomain> products = List.of();
  Map<Long, PriceDomain> cheapestByProductId = Map.of();
  Map<Long, StoreDomain> storeById = Map.of();
  Map<Long, ProductDomain> validProducts = Map.of();
  List<StoreVisitDomain> route = List.of();

  ShoppingContext(List<String> itemNames) {
    this.itemNames = itemNames;
  }
}
