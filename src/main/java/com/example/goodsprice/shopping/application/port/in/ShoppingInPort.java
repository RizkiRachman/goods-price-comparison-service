package com.example.goodsprice.shopping.application.port.in;

import com.example.goodsprice.shopping.application.domain.model.ShoppingOptimizationResult;
import java.util.List;

@FunctionalInterface
public interface ShoppingInPort {

  ShoppingOptimizationResult optimizeShoppingRoute(List<String> itemNames);
}
