package com.example.goodsprice.product.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.api.model.Product;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class ProductDtoMapperTest {

  private final ProductDtoMapper mapper = new ProductDtoMapper();

  @Test
  void shouldMapMinMaxPrices_whenIncludePriceIsTrue() {
    // Given
    var domain =
        ProductDomain.builder()
            .id(1L)
            .name("Test Product")
            .category("Food")
            .avgPrice(new BigDecimal("65000.00"))
            .minPrice(new BigDecimal("60000.00"))
            .maxPrice(new BigDecimal("70000.00"))
            .priceUpdatedAt(LocalDateTime.now())
            .build();

    // When
    Product result = mapper.toApiProduct(domain, true);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getDetail()).isNotNull();
    assertThat(result.getDetail().getPrice()).isNotNull();
    assertThat(result.getDetail().getPrice().getAvg()).isEqualTo(65000.00);
    assertThat(result.getDetail().getPrice().getMin()).isEqualTo(60000.00);
    assertThat(result.getDetail().getPrice().getMax()).isEqualTo(70000.00);
  }

  @Test
  void shouldNotMapPriceDetail_whenIncludePriceIsFalse() {
    // Given
    var domain =
        ProductDomain.builder()
            .id(1L)
            .name("Test Product")
            .avgPrice(new BigDecimal("65000.00"))
            .minPrice(new BigDecimal("60000.00"))
            .maxPrice(new BigDecimal("70000.00"))
            .build();

    // When
    Product result = mapper.toApiProduct(domain, false);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getDetail()).isNull();
  }
}
