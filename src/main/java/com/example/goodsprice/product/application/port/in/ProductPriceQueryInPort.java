package com.example.goodsprice.product.application.port.in;

import java.util.List;

@FunctionalInterface
public interface ProductPriceQueryInPort {

  List<Long> findProductIdsByStoreIds(List<Long> storeIds);
}
