package com.example.goodsprice.shopping.application.domain.service;

import com.example.goodsprice.shopping.application.domain.model.ShoppingOptimizationResult;
import com.example.goodsprice.shopping.application.domain.service.optimize.ShoppingOptimizer;
import com.example.goodsprice.shopping.application.port.in.ShoppingInPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingService implements ShoppingInPort {

  private final ShoppingOptimizer optimizer;

  @Override
  public ShoppingOptimizationResult optimizeShoppingRoute(List<String> itemNames) {
    return optimizer.optimize(itemNames);
  }
}
