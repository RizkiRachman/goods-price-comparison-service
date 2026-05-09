package com.example.goodsprice.product.application.port.in;

import java.util.List;

public interface ProductPriceQueryInPort {

  List<Long> findProductIdsByStoreIds(List<Long> storeIds);
}
