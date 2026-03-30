package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.ShoppingApi;
import com.example.goodsprice.api.model.ShoppingOptimizeRequest;
import com.example.goodsprice.api.model.ShoppingOptimizeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for shopping route optimization.
 */
@RestController
public class ShoppingController implements ShoppingApi {

    @Override
    public ResponseEntity<ShoppingOptimizeResponse> optimizeShoppingRoute(ShoppingOptimizeRequest request) {
        // TODO: Implement shopping optimization logic
        ShoppingOptimizeResponse response = new ShoppingOptimizeResponse();
        return ResponseEntity.ok(response);
    }
}
