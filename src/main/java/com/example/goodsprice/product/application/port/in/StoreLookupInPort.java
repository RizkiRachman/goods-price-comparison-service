package com.example.goodsprice.product.application.port.in;

import java.util.List;

@FunctionalInterface
public interface StoreLookupInPort {

  List<Long> findStoreIdsByName(String name);
}
