package com.example.goodsprice.price.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreatePriceRecordRequest;
import com.example.goodsprice.api.model.PriceRecord;
import com.example.goodsprice.api.model.PriceRecordListResponse;
import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.api.model.PriceSearchResponse;
import com.example.goodsprice.api.model.PriceSearchResponseV2;
import com.example.goodsprice.api.model.UpdatePriceRecordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class PriceControllerTest {

  @Mock private PriceWebAdapter adapter;

  @InjectMocks private PriceController controller;

  private PriceRecord priceRecord;
  private CreatePriceRecordRequest createRequest;
  private UpdatePriceRecordRequest updateRequest;

  @BeforeEach
  void setUp() {
    priceRecord = new PriceRecord();
    priceRecord.setId(1L);
    priceRecord.setPrice(15000.0);

    createRequest = new CreatePriceRecordRequest();
    createRequest.setStoreId(10L);
    createRequest.setPrice(15000.0);

    updateRequest = new UpdatePriceRecordRequest();
    updateRequest.setPrice(18000.0);
  }

  @Test
  @DisplayName("Should create price record and return 201")
  void shouldCreatePriceRecord() {
    when(adapter.createPriceRecord(100L, createRequest)).thenReturn(priceRecord);

    var response = controller.createPriceRecord(100L, createRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
    verify(adapter).createPriceRecord(100L, createRequest);
  }

  @Test
  @DisplayName("Should get price record and return 200")
  void shouldGetPriceRecord() {
    when(adapter.getPriceRecord(1L)).thenReturn(priceRecord);

    var response = controller.getPriceRecord(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
    verify(adapter).getPriceRecord(1L);
  }

  @Test
  @DisplayName("Should delete price record and return 204")
  void shouldDeletePriceRecord() {
    var response = controller.deletePriceRecord(1L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(adapter).deletePriceRecord(1L);
  }

  @Test
  @DisplayName("Should list product prices and return 200")
  void shouldListProductPrices() {
    var listResponse = new PriceRecordListResponse();
    when(adapter.listProductPrices(100L, null, null, null, null, null, null, null, null, null))
        .thenReturn(listResponse);

    var response =
        controller.listProductPrices(100L, null, null, null, null, null, null, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    verify(adapter).listProductPrices(100L, null, null, null, null, null, null, null, null, null);
  }

  @Test
  @DisplayName("Should search prices and return 200")
  void shouldSearchPrices() {
    var searchRequest = new PriceSearchRequest();
    var searchResponse = new PriceSearchResponse();
    when(adapter.search(searchRequest)).thenReturn(searchResponse);

    var response = controller.searchPrices(searchRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapter).search(searchRequest);
  }

  @Test
  @DisplayName("Should search prices v2 and return 200")
  void shouldSearchPricesV2() {
    var searchRequest = new PriceSearchRequestV2();
    var searchResponse = new PriceSearchResponseV2();
    when(adapter.searchV2(searchRequest)).thenReturn(searchResponse);

    var response = controller.searchPricesV2(searchRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapter).searchV2(searchRequest);
  }

  @Test
  @DisplayName("Should update price record and return 200")
  void shouldUpdatePriceRecord() {
    when(adapter.updatePriceRecord(1L, updateRequest)).thenReturn(priceRecord);

    var response = controller.updatePriceRecord(1L, updateRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
    verify(adapter).updatePriceRecord(1L, updateRequest);
  }
}
