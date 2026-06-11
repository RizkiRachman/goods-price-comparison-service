package com.example.goodsprice.store.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreateStoreRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.api.model.StoreListResponse;
import com.example.goodsprice.api.model.UpdateStoreRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

  @Mock private StoreWebAdapter adapter;

  @InjectMocks private StoreController controller;

  private Store apiStore;
  private StoreListResponse listResponse;

  @BeforeEach
  void setUp() {
    apiStore = new Store();
    apiStore.setId(1L);
    apiStore.setName("Toko Segar");

    listResponse = new StoreListResponse();
  }

  @Test
  @DisplayName("Should create store via controller")
  void shouldCreateStore() {
    var request = new CreateStoreRequest();
    request.setName("Toko Segar");

    when(adapter.create(request)).thenReturn(apiStore);

    var response = controller.createStore(request);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
    verify(adapter).create(request);
  }

  @Test
  @DisplayName("Should get store by id")
  void shouldGetStore() {
    when(adapter.getById(1L)).thenReturn(apiStore);

    var response = controller.getStore(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
    verify(adapter).getById(1L);
  }

  @Test
  @DisplayName("Should list stores")
  void shouldListStores() {
    when(adapter.list(1, 20, "name", "asc", "search", EntityStatus.APPROVED, "chain", "loc"))
        .thenReturn(listResponse);

    var response =
        controller.listStores(
            1, 20, "search", "loc", "chain", EntityStatus.APPROVED, "name", "asc");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @DisplayName("Should update store")
  void shouldUpdateStore() {
    var request = new UpdateStoreRequest();
    request.setName("Updated Store");

    when(adapter.update(1L, request)).thenReturn(apiStore);

    var response = controller.updateStore(1L, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapter).update(1L, request);
  }

  @Test
  @DisplayName("Should delete store")
  void shouldDeleteStore() {
    var response = controller.deleteStore(1L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(adapter).delete(1L);
  }
}
