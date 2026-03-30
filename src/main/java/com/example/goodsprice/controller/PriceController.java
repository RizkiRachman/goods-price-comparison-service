package com.example.goodsprice.controller;

import com.example.goodsprice.api.controller.PricesApi;
import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.api.model.PriceSearchResponse;
import com.example.goodsprice.api.model.PriceSearchResponseV2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for price search and comparison.
 * Implements both v1 and v2 API endpoints.
 */
@RestController
public class PriceController implements PricesApi {

    @Override
    public ResponseEntity<PriceSearchResponse> searchPrices(PriceSearchRequest request) {
        // TODO: Implement price search logic for v1
        PriceSearchResponse response = new PriceSearchResponse();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PriceSearchResponseV2> searchPricesV2(PriceSearchRequestV2 request) {
        // TODO: Implement price search logic for v2
        PriceSearchResponseV2 response = new PriceSearchResponseV2();
        return ResponseEntity.ok(response);
    }
}
