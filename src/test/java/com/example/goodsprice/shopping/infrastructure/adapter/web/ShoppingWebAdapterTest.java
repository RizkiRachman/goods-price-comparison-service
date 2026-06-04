package com.example.goodsprice.shopping.infrastructure.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.api.model.ShoppingOptimizeRequest;
import com.example.goodsprice.api.model.StoreVisit;
import com.example.goodsprice.shopping.application.domain.model.ShoppingOptimizationResult;
import com.example.goodsprice.shopping.application.domain.model.ShoppingSavingsDomain;
import com.example.goodsprice.shopping.application.domain.model.StoreVisitDomain;
import com.example.goodsprice.shopping.application.port.in.ShoppingInPort;
import com.example.goodsprice.shopping.infrastructure.adapter.web.mapper.ShoppingDtoMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingWebAdapterTest {

  @Mock private ShoppingInPort shoppingInPort;
  @Mock private ShoppingDtoMapper mapper;

  @InjectMocks private ShoppingWebAdapter adapter;

  private ShoppingOptimizationResult result;
  private StoreVisitDomain visitDomain;
  private StoreVisit storeVisitApi;

  @BeforeEach
  void setUp() {
    visitDomain =
        StoreVisitDomain.builder().storeId(10L).storeName("Store A").subtotal(5.0).build();
    storeVisitApi = new StoreVisit();
    storeVisitApi.setStoreId(10L);
    storeVisitApi.setStoreName("Store A");

    result =
        ShoppingOptimizationResult.builder()
            .totalItems(2)
            .totalCost(10.0)
            .storesToVisit(2)
            .route(List.of(visitDomain))
            .savings(
                ShoppingSavingsDomain.builder()
                    .comparedToSingleStore(5.0)
                    .percentage(33.33)
                    .build())
            .build();
  }

  @Test
  @DisplayName("Should optimize shopping route")
  void shouldOptimize() {
    var request = new ShoppingOptimizeRequest();
    request.setItems(List.of("Apple", "Bread"));

    when(shoppingInPort.optimizeShoppingRoute(List.of("Apple", "Bread"))).thenReturn(result);
    when(mapper.toStoreVisits(List.of(visitDomain))).thenReturn(List.of(storeVisitApi));
    when(mapper.toShoppingSavings(result.getSavings()))
        .thenReturn(new com.example.goodsprice.api.model.ShoppingSavings());

    var response = adapter.optimize(request);

    assertNotNull(response);
    assertEquals(2, response.getTotalItems());
    assertEquals(10.0, response.getTotalCost());
    assertEquals(2, response.getStoresToVisit());
    assertEquals(1, response.getRoute().size());
    verify(shoppingInPort).optimizeShoppingRoute(List.of("Apple", "Bread"));
    verify(mapper).toStoreVisits(List.of(visitDomain));
  }

  @Test
  @DisplayName("Should handle null request")
  void shouldHandleNullRequest() {
    when(shoppingInPort.optimizeShoppingRoute(null))
        .thenReturn(
            ShoppingOptimizationResult.builder()
                .totalItems(0)
                .totalCost(0.0)
                .storesToVisit(0)
                .route(List.of())
                .build());
    when(mapper.toStoreVisits(List.of())).thenReturn(List.of());

    var response = adapter.optimize(null);

    assertNotNull(response);
    assertEquals(0, response.getTotalItems());
    assertEquals(0.0, response.getTotalCost());
    verify(shoppingInPort).optimizeShoppingRoute(null);
  }
}
