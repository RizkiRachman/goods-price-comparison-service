package com.example.goodsprice.shopping.infrastructure.adapter.web;

import com.example.goodsprice.api.model.ShoppingOptimizeRequest;
import com.example.goodsprice.api.model.ShoppingOptimizeResponse;
import com.example.goodsprice.shopping.application.port.in.ShoppingInPort;
import com.example.goodsprice.shopping.infrastructure.adapter.web.mapper.ShoppingDtoMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShoppingWebAdapter {

  private final ShoppingInPort shoppingInPort;
  private final ShoppingDtoMapper mapper;

  public ShoppingOptimizeResponse optimize(ShoppingOptimizeRequest request) {
    var items = Objects.nonNull(request) ? request.getItems() : null;
    var result = shoppingInPort.optimizeShoppingRoute(items);

    return new ShoppingOptimizeResponse()
        .totalItems(result.getTotalItems())
        .totalCost(result.getTotalCost())
        .storesToVisit(result.getStoresToVisit())
        .route(mapper.toStoreVisits(result.getRoute()))
        .savings(mapper.toShoppingSavings(result.getSavings()));
  }
}
