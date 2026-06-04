package com.example.goodsprice.price.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductPriceQueryServiceTest {

  @Mock private PriceRepositoryPort priceRepository;

  @InjectMocks private ProductPriceQueryService service;

  @Test
  @DisplayName("Should return empty list when storeIds is null")
  void shouldReturnEmptyWhenStoreIdsIsNull() {
    assertTrue(service.findProductIdsByStoreIds(null).isEmpty());
  }

  @Test
  @DisplayName("Should return empty list when storeIds is empty")
  void shouldReturnEmptyWhenStoreIdsIsEmpty() {
    assertTrue(service.findProductIdsByStoreIds(List.of()).isEmpty());
  }

  @Test
  @DisplayName("Should return product ids from repository")
  void shouldReturnProductIdsFromRepository() {
    when(priceRepository.findProductIdsByStoreIds(List.of(1L, 2L))).thenReturn(List.of(100L, 200L));

    var result = service.findProductIdsByStoreIds(List.of(1L, 2L));

    assertEquals(List.of(100L, 200L), result);
    verify(priceRepository).findProductIdsByStoreIds(List.of(1L, 2L));
  }

  @Test
  @DisplayName("Should return empty list when repository returns empty")
  void shouldReturnEmptyWhenRepositoryReturnsEmpty() {
    when(priceRepository.findProductIdsByStoreIds(List.of(999L))).thenReturn(List.of());

    var result = service.findProductIdsByStoreIds(List.of(999L));

    assertTrue(result.isEmpty());
    verify(priceRepository).findProductIdsByStoreIds(List.of(999L));
  }
}
