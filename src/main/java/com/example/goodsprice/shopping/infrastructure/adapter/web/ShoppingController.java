package com.example.goodsprice.shopping.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.ShoppingApi;
import com.example.goodsprice.api.model.ShoppingOptimizeRequest;
import com.example.goodsprice.api.model.ShoppingOptimizeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShoppingController implements ShoppingApi {

  private final ShoppingWebAdapter adapter;

  @Override
  public ResponseEntity<ShoppingOptimizeResponse> optimizeShoppingRoute(
      ShoppingOptimizeRequest request) {
    return ResponseEntity.ok(adapter.optimize(request));
  }
}
