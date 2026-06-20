package com.example.goodsprice.store.infrastructure.adapter.web;

import static com.example.goodsprice.common.web.ControllerResponse.created;
import static com.example.goodsprice.common.web.ControllerResponse.noContent;
import static com.example.goodsprice.common.web.ControllerResponse.ok;

import com.example.goodsprice.api.controller.StoresApi;
import com.example.goodsprice.api.model.CreateStoreRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.api.model.StoreListResponse;
import com.example.goodsprice.api.model.UpdateStoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreController implements StoresApi {

  private final StoreWebAdapter adapter;

  @Override
  public ResponseEntity<Store> createStore(@Valid CreateStoreRequest request) {
    return created(adapter.create(request));
  }

  @Override
  public ResponseEntity<Void> deleteStore(Long storeId) {
    adapter.delete(storeId);
    return noContent();
  }

  @Override
  public ResponseEntity<Store> getStore(Long storeId) {
    return ok(adapter.getById(storeId));
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
    return ok(adapter.list(page, pageSize, sortBy, sortOrder, search, status, chain, location));
  }

  @Override
  public ResponseEntity<Store> updateStore(Long storeId, @Valid UpdateStoreRequest request) {
    return ok(adapter.update(storeId, request));
  }
}
