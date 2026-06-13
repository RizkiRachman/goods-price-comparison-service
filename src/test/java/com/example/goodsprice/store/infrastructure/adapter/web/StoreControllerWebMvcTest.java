package com.example.goodsprice.store.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.CreateStoreRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Store;
import com.example.goodsprice.api.model.StoreListResponse;
import com.example.goodsprice.api.model.UpdateStoreRequest;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.web.AbstractControllerWebMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class StoreControllerWebMvcTest extends AbstractControllerWebMvcTest {

  @Mock private StoreWebAdapter adapter;

  @Override
  protected Object getController() {
    return new StoreController(adapter);
  }

  private Store createApiStore() {
    var store = new Store();
    store.setId(1L);
    store.setName("Toko Makmur");
    store.setLocation("Jakarta");
    store.setStatus(EntityStatus.APPROVED);
    return store;
  }

  @Test
  @DisplayName("POST /v1/stores should return 201 Created")
  void shouldCreateStoreReturns201() throws Exception {
    var store = createApiStore();
    when(adapter.create(any(CreateStoreRequest.class))).thenReturn(store);

    var request = new CreateStoreRequest();
    request.setName("Toko Makmur");
    request.setLocation("Jakarta");

    mockMvc
        .perform(
            post("/v1/stores").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Toko Makmur"));
  }

  @Test
  @DisplayName("GET /v1/stores/{storeId} should return 200 OK")
  void shouldGetStoreReturns200() throws Exception {
    var store = createApiStore();
    when(adapter.getById(1L)).thenReturn(store);

    mockMvc
        .perform(get("/v1/stores/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Toko Makmur"));
  }

  @Test
  @DisplayName("GET /v1/stores should return 200 OK with paginated list")
  void shouldListStoresReturns200() throws Exception {
    var listResponse = new StoreListResponse();
    when(adapter.list(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(listResponse);

    mockMvc
        .perform(
            get("/v1/stores")
                .param("page", "1")
                .param("pageSize", "20")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /v1/stores/{storeId} should return 200 OK")
  void shouldUpdateStoreReturns200() throws Exception {
    var store = createApiStore();
    when(adapter.update(eq(1L), any(UpdateStoreRequest.class))).thenReturn(store);

    var updateRequest = new UpdateStoreRequest();
    updateRequest.setName("Toko Makmur Updated");

    mockMvc
        .perform(
            put("/v1/stores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("DELETE /v1/stores/{storeId} should return 204 No Content")
  void shouldDeleteStoreReturns204() throws Exception {
    mockMvc
        .perform(delete("/v1/stores/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("GET /v1/stores/{storeId} should return 404 when store not found")
  void shouldReturn404WhenStoreNotFound() throws Exception {
    when(adapter.getById(999L))
        .thenThrow(new NotFoundException("STORE_NOT_FOUND", "Store not found with id: 999"));

    mockMvc
        .perform(get("/v1/stores/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("STORE_NOT_FOUND"));
  }

  @Test
  @DisplayName("POST /v1/stores should return 400 when name is null")
  void shouldReturn400WhenNameIsNull() throws Exception {
    String invalidJson =
        """
        {"name": null, "location": "Jakarta"}
        """;

    mockMvc
        .perform(post("/v1/stores").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /v1/stores/{storeId} should return correct JSON structure")
  void shouldReturnCorrectJsonStructure() throws Exception {
    var store = createApiStore();
    when(adapter.getById(1L)).thenReturn(store);

    mockMvc
        .perform(get("/v1/stores/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Toko Makmur"))
        .andExpect(jsonPath("$.location").value("Jakarta"))
        .andExpect(jsonPath("$.status").value("approved"));
  }
}
