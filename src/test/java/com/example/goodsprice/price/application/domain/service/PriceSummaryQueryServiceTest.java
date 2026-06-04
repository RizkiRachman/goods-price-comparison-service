package com.example.goodsprice.price.application.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.application.port.out.PriceSummaryRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceSummaryQueryServiceTest {

  @Mock private PriceSummaryRepositoryPort priceSummaryRepository;

  @InjectMocks private PriceSummaryQueryService service;

  @Test
  @DisplayName("Should return empty list when productIds is null")
  void shouldReturnEmptyWhenProductIdsIsNull() {
    assertTrue(service.findByProductIds(null).isEmpty());
  }

  @Test
  @DisplayName("Should return empty list when productIds is empty")
  void shouldReturnEmptyWhenProductIdsIsEmpty() {
    assertTrue(service.findByProductIds(Set.of()).isEmpty());
  }

  @Test
  @DisplayName("Should return summaries from repository")
  void shouldReturnSummariesFromRepository() {
    var summary =
        ProductPriceSummary.builder()
            .productId(1L)
            .avgPrice(new BigDecimal("10.00"))
            .lastCalculatedAt(LocalDateTime.now())
            .build();
    when(priceSummaryRepository.findByProductIds(Set.of(1L, 2L))).thenReturn(List.of(summary));

    var result = service.findByProductIds(Set.of(1L, 2L));

    assertEquals(1, result.size());
    assertEquals(1L, result.getFirst().getProductId());
    verify(priceSummaryRepository).findByProductIds(Set.of(1L, 2L));
  }

  @Test
  @DisplayName("Should return empty list when repository returns empty")
  void shouldReturnEmptyWhenRepositoryReturnsEmpty() {
    when(priceSummaryRepository.findByProductIds(Set.of(999L))).thenReturn(List.of());

    var result = service.findByProductIds(Set.of(999L));

    assertTrue(result.isEmpty());
    verify(priceSummaryRepository).findByProductIds(Set.of(999L));
  }
}
