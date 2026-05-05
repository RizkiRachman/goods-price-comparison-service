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

    var response = new ShoppingOptimizeResponse();
    response.setTotalItems(result.getTotalItems());
    response.setTotalCost(result.getTotalCost());
    response.setStoresToVisit(result.getStoresToVisit());
    response.setRoute(mapper.toStoreVisits(result.getRoute()));
    response.setSavings(mapper.toShoppingSavings(result.getSavings()));
    return response;
  }
}
