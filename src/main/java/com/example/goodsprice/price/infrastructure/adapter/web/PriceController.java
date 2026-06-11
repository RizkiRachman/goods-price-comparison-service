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
import jakarta.validation.Valid;
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
      Long productId, @Valid CreatePriceRecordRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(adapter.createPriceRecord(productId, request));
  }

  @Override
  public ResponseEntity<PriceRecord> getPriceRecord(Long id) {
    return ResponseEntity.ok(adapter.getPriceRecord(id));
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
    return ResponseEntity.ok(
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
            sortDirection));
  }

  @Override
  public ResponseEntity<PriceSearchResponse> searchPrices(@Valid PriceSearchRequest request) {
    return ResponseEntity.ok(adapter.search(request));
  }

  @Override
  public ResponseEntity<PriceSearchResponseV2> searchPricesV2(@Valid PriceSearchRequestV2 request) {
    return ResponseEntity.ok(adapter.searchV2(request));
  }

  @Override
  public ResponseEntity<PriceRecord> updatePriceRecord(
      Long id, @Valid UpdatePriceRecordRequest request) {
    return ResponseEntity.ok(adapter.updatePriceRecord(id, request));
  }
}
