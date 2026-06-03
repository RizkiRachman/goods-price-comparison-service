package com.example.goodsprice.price.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.DateRange;
import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.price.infrastructure.adapter.web.mapper.PriceDtoMapper;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeEach
  void setUp() {
    product = ProductDomain.builder().id(1L).name("Susu Kotak").build();
    price = PriceDomain.builder().id(10L).productId(1L).storeId(100L).price(15000.0).build();
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenProductNameIsNullInSearch() {
    var request = new PriceSearchRequest();
    request.setProductName(null);

    var exception =
        assertThrows(IllegalArgumentException.class, () -> priceWebAdapter.search(request));

    assertEquals("Product name must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenProductNotFoundInSearch() {
    var request = new PriceSearchRequest();
    request.setProductName("NonExistent");
    when(productInPort.searchByName("NonExistent")).thenReturn(List.of());

    assertThrows(NotFoundException.class, () -> priceWebAdapter.search(request));
  }

  @Test
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
  void shouldThrowIllegalArgumentExceptionWhenProductNameIsNullInSearchV2() {
    var request = new PriceSearchRequestV2();
    request.setProductName(null);

    var exception =
        assertThrows(IllegalArgumentException.class, () -> priceWebAdapter.searchV2(request));

    assertEquals("Product name must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenProductNotFoundInSearchV2() {
    var request = new PriceSearchRequestV2();
    request.setProductName("NonExistent");
    when(productInPort.searchByName("NonExistent")).thenReturn(List.of());

    assertThrows(NotFoundException.class, () -> priceWebAdapter.searchV2(request));
  }

  @Test
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
}
