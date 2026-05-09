package com.example.goodsprice.product.application.port.in;

import java.util.List;

public interface StoreLookupInPort {

  List<Long> findStoreIdsByName(String name);
}
