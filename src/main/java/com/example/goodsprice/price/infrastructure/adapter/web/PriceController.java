package com.example.goodsprice.price.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.PricesApi;
import com.example.goodsprice.api.model.CreatePriceRecordRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.PriceRecord;
import com.example.goodsprice.api.model.PriceRecordListResponse;
import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.api.model.PriceSearchResponse;
import com.example.goodsprice.api.model.PriceSearchResponseV2;
import com.example.goodsprice.api.model.UpdatePriceRecordRequest;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PriceController implements PricesApi {

  private final PriceWebAdapter adapter;

  @Override
  public ResponseEntity<PriceRecord> createPriceRecord(
      Long productId, CreatePriceRecordRequest request) {
    var result = adapter.createPriceRecord(productId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Override
  public ResponseEntity<PriceRecord> getPriceRecord(Long id) {
    var result = adapter.getPriceRecord(id);
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<Void> deletePriceRecord(Long id) {
    adapter.deletePriceRecord(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<PriceRecordListResponse> listProductPrices(
      Long productId,
      Long storeId,
      OffsetDateTime startDate,
      OffsetDateTime endDate,
      Boolean isPromo,
      EntityStatus status,
      Integer page,
      Integer size,
      String sortBy,
      String sortDirection) {
    var result =
        adapter.listProductPrices(
            productId,
            storeId,
            startDate,
            endDate,
            isPromo,
            status,
            page,
            size,
            sortBy,
            sortDirection);
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<PriceSearchResponse> searchPrices(PriceSearchRequest request) {
    return ResponseEntity.ok(adapter.search(request));
  }

  @Override
  public ResponseEntity<PriceSearchResponseV2> searchPricesV2(PriceSearchRequestV2 request) {
    return ResponseEntity.ok(adapter.searchV2(request));
  }

  @Override
  public ResponseEntity<PriceRecord> updatePriceRecord(Long id, UpdatePriceRecordRequest request) {
    var result = adapter.updatePriceRecord(id, request);
    return ResponseEntity.ok(result);
  }
}
