package com.example.goodsprice.price.infrastructure.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.goodsprice.api.model.CreatePriceRecordRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.PriceRecord;
import com.example.goodsprice.api.model.PriceRecordListResponse;
import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.api.model.PriceSearchResponse;
import com.example.goodsprice.api.model.PriceSearchResponseV2;
import com.example.goodsprice.api.model.UpdatePriceRecordRequest;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.web.AbstractControllerWebMvcTest;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class PriceControllerWebMvcTest extends AbstractControllerWebMvcTest {

  @Mock private PriceWebAdapter adapter;

  @Override
  protected Object getController() {
    return new PriceController(adapter);
  }

  private PriceRecord createPriceRecord() {
    var record = new PriceRecord();
    record.setId(1L);
    record.setPrice(15000.0);
    record.setStoreId(10L);
    return record;
  }

  @Test
  @DisplayName("POST /v1/products/{productId}/prices should return 201 Created")
  void shouldCreatePriceRecordReturns201() throws Exception {
    var record = createPriceRecord();
    when(adapter.createPriceRecord(eq(100L), any(CreatePriceRecordRequest.class)))
        .thenReturn(record);

    var request = new CreatePriceRecordRequest();
    request.setStoreId(10L);
    request.setPrice(15000.0);

    mockMvc
        .perform(
            post("/v1/products/{productId}/prices", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.price").value(15000.0));
  }

  @Test
  @DisplayName("GET /v1/prices/{id} should return 200 OK")
  void shouldGetPriceRecordReturns200() throws Exception {
    var record = createPriceRecord();
    when(adapter.getPriceRecord(1L)).thenReturn(record);

    mockMvc
        .perform(get("/v1/prices/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.price").value(15000.0));
  }

  @Test
  @DisplayName("DELETE /v1/prices/{id} should return 204 No Content")
  void shouldDeletePriceRecordReturns204() throws Exception {
    mockMvc
        .perform(delete("/v1/prices/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("GET /v1/products/{productId}/prices should return 200 OK with list")
  void shouldListProductPricesReturns200() throws Exception {
    var listResponse = new PriceRecordListResponse();
    when(adapter.listProductPrices(
            nullable(Long.class),
            nullable(Long.class),
            nullable(OffsetDateTime.class),
            nullable(OffsetDateTime.class),
            nullable(Boolean.class),
            nullable(EntityStatus.class),
            nullable(Integer.class),
            nullable(Integer.class),
            nullable(String.class),
            nullable(String.class)))
        .thenReturn(listResponse);

    mockMvc
        .perform(
            get("/v1/products/{productId}/prices", 100L)
                .param("page", "1")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("PUT /v1/prices/{id} should return 200 OK")
  void shouldUpdatePriceRecordReturns200() throws Exception {
    var record = createPriceRecord();
    when(adapter.updatePriceRecord(eq(1L), any(UpdatePriceRecordRequest.class))).thenReturn(record);

    var updateRequest = new UpdatePriceRecordRequest();
    updateRequest.setPrice(18000.0);

    mockMvc
        .perform(
            put("/v1/prices/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("POST /v1/prices/search should return 200 OK")
  void shouldSearchPricesReturns200() throws Exception {
    var searchResponse = new PriceSearchResponse();
    when(adapter.search(any(PriceSearchRequest.class))).thenReturn(searchResponse);

    var request = new PriceSearchRequest();
    request.setProductName("Test Product");

    mockMvc
        .perform(
            post("/v1/prices/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /v2/prices/search should return 200 OK")
  void shouldSearchPricesV2Returns200() throws Exception {
    var searchResponse = new PriceSearchResponseV2();
    when(adapter.searchV2(any(PriceSearchRequestV2.class))).thenReturn(searchResponse);

    var request = new PriceSearchRequestV2();
    request.setProductName("Test Product");

    mockMvc
        .perform(
            post("/v2/prices/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /v1/prices/{id} should return 404 when price not found")
  void shouldReturn404WhenPriceNotFound() throws Exception {
    when(adapter.getPriceRecord(999L))
        .thenThrow(new NotFoundException("PRICE_NOT_FOUND", "Price not found with id: 999"));

    mockMvc
        .perform(get("/v1/prices/{id}", 999L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("PRICE_NOT_FOUND"));
  }

  @Test
  @DisplayName(
      "POST /v1/products/{productId}/prices should return 400 when request body is invalid")
  void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/v1/products/{productId}/prices", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /v1/prices/{id} should return correct JSON structure")
  void shouldReturnCorrectJsonStructure() throws Exception {
    var record = createPriceRecord();
    when(adapter.getPriceRecord(1L)).thenReturn(record);

    mockMvc
        .perform(get("/v1/prices/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.price").value(15000.0))
        .andExpect(jsonPath("$.storeId").value(10));
  }
}
