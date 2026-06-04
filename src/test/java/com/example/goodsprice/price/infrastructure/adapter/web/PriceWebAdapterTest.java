package com.example.goodsprice.price.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.CreatePriceRecordRequest;
import com.example.goodsprice.api.model.DateRange;
import com.example.goodsprice.api.model.PriceRecord;
import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.api.model.UpdatePriceRecordRequest;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.price.infrastructure.adapter.web.mapper.PriceDtoMapper;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceWebAdapterTest {

  @Mock private PriceInPort priceInPort;
  @Mock private ProductInPort productInPort;
  @Mock private StoreInPort storeInPort;
  @Mock private PriceDtoMapper mapper;

  @InjectMocks private PriceWebAdapter priceWebAdapter;

  private ProductDomain product;
  private PriceDomain price;
  private StoreDomain store;

  @BeforeEach
  void setUp() {
    product = ProductDomain.builder().id(1L).name("Susu Kotak").build();
    price = PriceDomain.builder().id(10L).productId(1L).storeId(100L).price(15000.0).build();
    store = StoreDomain.builder().id(100L).name("Toko Segar").build();
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when product name is null in search")
  void shouldThrowIllegalArgumentExceptionWhenProductNameIsNullInSearch() {
    var request = new PriceSearchRequest();
    request.setProductName(null);

    var exception =
        assertThrows(IllegalArgumentException.class, () -> priceWebAdapter.search(request));

    assertEquals("Product name must not be null", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw NotFoundException when product not found in search")
  void shouldThrowNotFoundExceptionWhenProductNotFoundInSearch() {
    var request = new PriceSearchRequest();
    request.setProductName("NonExistent");
    when(productInPort.searchByName("NonExistent")).thenReturn(List.of());

    assertThrows(NotFoundException.class, () -> priceWebAdapter.search(request));
  }

  @Test
  @DisplayName("Should succeed when product found in search")
  void shouldSucceedWhenProductFoundInSearch() {
    var request = new PriceSearchRequest();
    request.setProductName("Susu Kotak");
    var dateRange = new DateRange();
    dateRange.setFrom(LocalDate.now().minusDays(7));
    dateRange.setTo(LocalDate.now());
    request.setDateRange(dateRange);

    when(productInPort.searchByName("Susu Kotak")).thenReturn(List.of(product));
    when(priceInPort.searchByProduct(eq(1L), any(), any())).thenReturn(List.of(price));
    when(storeInPort.findAllById(any())).thenReturn(List.of());

    var response = priceWebAdapter.search(request);

    assertNotNull(response);
    assertEquals("Susu Kotak", response.getProductName());
    verify(productInPort).searchByName("Susu Kotak");
    verify(priceInPort).searchByProduct(eq(1L), eq(dateRange.getFrom()), eq(dateRange.getTo()));
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when product name is null in searchV2")
  void shouldThrowIllegalArgumentExceptionWhenProductNameIsNullInSearchV2() {
    var request = new PriceSearchRequestV2();
    request.setProductName(null);

    var exception =
        assertThrows(IllegalArgumentException.class, () -> priceWebAdapter.searchV2(request));

    assertEquals("Product name must not be null", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw NotFoundException when product not found in searchV2")
  void shouldThrowNotFoundExceptionWhenProductNotFoundInSearchV2() {
    var request = new PriceSearchRequestV2();
    request.setProductName("NonExistent");
    when(productInPort.searchByName("NonExistent")).thenReturn(List.of());

    assertThrows(NotFoundException.class, () -> priceWebAdapter.searchV2(request));
  }

  @Test
  @DisplayName("Should succeed when product found in searchV2")
  void shouldSucceedWhenProductFoundInSearchV2() {
    var request = new PriceSearchRequestV2();
    request.setProductName("Susu Kotak");
    var dateRange = new DateRange();
    dateRange.setFrom(LocalDate.now().minusDays(7));
    dateRange.setTo(LocalDate.now());
    request.setDateRange(dateRange);

    when(productInPort.searchByName("Susu Kotak")).thenReturn(List.of(product));
    when(priceInPort.searchByProduct(eq(1L), any(), any())).thenReturn(List.of(price));
    when(storeInPort.findAllById(any())).thenReturn(List.of());

    var response = priceWebAdapter.searchV2(request);

    assertNotNull(response);
    assertEquals("Susu Kotak", response.getProductName());
    verify(productInPort).searchByName("Susu Kotak");
    verify(priceInPort).searchByProduct(eq(1L), eq(dateRange.getFrom()), eq(dateRange.getTo()));
  }

  @Test
  @DisplayName("Should create price record")
  void shouldCreatePriceRecord() {
    var request = new CreatePriceRecordRequest();
    request.setStoreId(100L);
    request.setPrice(15000.0);
    request.setUnitPrice(15000.0);

    var priceRecord = new PriceRecord();
    priceRecord.setId(10L);
    priceRecord.setPrice(15000.0);

    when(priceInPort.create(eq(1L), eq(100L), eq(15000.0), eq(15000.0), any(), eq(false)))
        .thenReturn(price);
    when(storeInPort.findById(100L)).thenReturn(store);
    when(mapper.toPriceRecord(price, store)).thenReturn(priceRecord);

    var result = priceWebAdapter.createPriceRecord(1L, request);

    assertNotNull(result);
    assertEquals(10L, result.getId());
    verify(priceInPort).create(eq(1L), eq(100L), eq(15000.0), eq(15000.0), any(), eq(false));
    verify(storeInPort).findById(100L);
    verify(mapper).toPriceRecord(price, store);
  }

  @Test
  @DisplayName("Should get price record")
  void shouldGetPriceRecord() {
    var priceRecord = new PriceRecord();
    priceRecord.setId(10L);
    priceRecord.setPrice(15000.0);

    when(priceInPort.findById(10L)).thenReturn(price);
    when(storeInPort.findById(100L)).thenReturn(store);
    when(mapper.toPriceRecord(price, store)).thenReturn(priceRecord);

    var result = priceWebAdapter.getPriceRecord(10L);

    assertNotNull(result);
    assertEquals(10L, result.getId());
    verify(priceInPort).findById(10L);
    verify(storeInPort).findById(100L);
  }

  @Test
  @DisplayName("Should delete price record")
  void shouldDeletePriceRecord() {
    priceWebAdapter.deletePriceRecord(10L);

    verify(priceInPort).deleteById(10L);
  }

  @Test
  @DisplayName("Should update price record")
  void shouldUpdatePriceRecord() {
    var request = new UpdatePriceRecordRequest();
    request.setPrice(18000.0);

    var priceRecord = new PriceRecord();
    priceRecord.setId(10L);
    priceRecord.setPrice(18000.0);

    when(priceInPort.update(eq(10L), eq(18000.0), any(), any(), any())).thenReturn(price);
    when(storeInPort.findById(100L)).thenReturn(store);
    when(mapper.toPriceRecord(price, store)).thenReturn(priceRecord);

    var result = priceWebAdapter.updatePriceRecord(10L, request);

    assertNotNull(result);
    assertEquals(10L, result.getId());
    verify(priceInPort).update(eq(10L), eq(18000.0), any(), any(), any());
    verify(storeInPort).findById(100L);
    verify(mapper).toPriceRecord(price, store);
  }

  @Test
  @DisplayName("Should list product prices with pagination")
  void shouldListProductPrices() {
    var pageSize = 10;

    var priceRecord = new PriceRecord();
    priceRecord.setId(10L);
    priceRecord.setPrice(15000.0);

    when(priceInPort.searchByProduct(any()))
        .thenReturn(
            new com.example.goodsprice.common.dto.PageResponse<>(
                List.of(price), 0, pageSize, 1, 1, true, true));
    when(storeInPort.findAllById(anyList())).thenReturn(List.of(store));
    when(mapper.toPriceRecord(price, store)).thenReturn(priceRecord);

    var result =
        priceWebAdapter.listProductPrices(
            1L, null, null, null, null, null, 1, pageSize, "dateRecorded", "desc");

    assertNotNull(result);
    assertNotNull(result.getData());
    verify(priceInPort).searchByProduct(any());
  }
}
