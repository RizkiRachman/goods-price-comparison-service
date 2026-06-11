package com.example.goodsprice.store.infrastructure.adapter.web;

import com.example.goodsprice.api.controller.StoresApi;
import com.example.goodsprice.api.model.CreateStoreRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.api.model.StoreListResponse;
import com.example.goodsprice.api.model.UpdateStoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreController implements StoresApi {

  private final StoreWebAdapter adapter;

  @Override
  public ResponseEntity<Store> createStore(@Valid CreateStoreRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
  }

  @Override
  public ResponseEntity<Void> deleteStore(Long storeId) {
    adapter.delete(storeId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Store> getStore(Long storeId) {
    return ResponseEntity.ok(adapter.getById(storeId));
  }

  @Override
  public ResponseEntity<StoreListResponse> listStores(
      Integer page,
      Integer pageSize,
      String search,
      String location,
      String chain,
      EntityStatus status,
      String sortBy,
      String sortOrder) {
    return ResponseEntity.ok(
        adapter.list(page, pageSize, sortBy, sortOrder, search, status, chain, location));
  }

  @Override
  public ResponseEntity<Store> updateStore(Long storeId, @Valid UpdateStoreRequest request) {
    return ResponseEntity.ok(adapter.update(storeId, request));
  }
}
