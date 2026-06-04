package com.example.goodsprice.shopping.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.shopping.application.domain.model.ShoppingItemDomain;
import com.example.goodsprice.shopping.application.domain.model.ShoppingSavingsDomain;
import com.example.goodsprice.shopping.application.domain.model.StoreVisitDomain;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("checkstyle:MethodName")
class ShoppingDtoMapperTest {

  private final ShoppingDtoMapper mapper = Mappers.getMapper(ShoppingDtoMapper.class);

  @Test
  void shouldMapStoreVisitDomain() {
    var domain =
        StoreVisitDomain.builder()
            .storeId(10L)
            .storeName("Store A")
            .storeLocation("Jakarta")
            .subtotal(15.0)
            .estimatedTime("10 min")
            .items(List.of())
            .build();

    var result = mapper.toStoreVisit(domain);

    assertThat(result).isNotNull();
    assertThat(result.getStoreId()).isEqualTo(10L);
    assertThat(result.getStoreName()).isEqualTo("Store A");
    assertThat(result.getSubtotal()).isEqualTo(15.0);
  }

  @Test
  void shouldMapStoreVisitList() {
    var domain =
        StoreVisitDomain.builder().storeId(10L).storeName("Store A").subtotal(15.0).build();

    var results = mapper.toStoreVisits(List.of(domain));

    assertThat(results).isNotNull();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getStoreId()).isEqualTo(10L);
  }

  @Test
  void shouldReturnEmptyListForNullStoreVisits() {
    assertThat(mapper.toStoreVisits(null)).isEmpty();
  }

  @Test
  void shouldMapShoppingItem() {
    var domain =
        ShoppingItemDomain.builder()
            .productName("Apple")
            .price(5.0)
            .quantity(2.0)
            .unit("KG")
            .build();

    var result = mapper.toShoppingItem(domain);

    assertThat(result).isNotNull();
    assertThat(result.getProductName()).isEqualTo("Apple");
    assertThat(result.getPrice()).isEqualTo(5.0);
  }

  @Test
  void shouldMapShoppingItemList() {
    var domain = ShoppingItemDomain.builder().productName("Apple").price(5.0).build();

    var results = mapper.toShoppingItems(List.of(domain));

    assertThat(results).isNotNull();
    assertThat(results).hasSize(1);
  }

  @Test
  void shouldReturnEmptyListForNullShoppingItems() {
    assertThat(mapper.toShoppingItems(null)).isEmpty();
  }

  @Test
  void shouldMapShoppingSavings() {
    var domain =
        ShoppingSavingsDomain.builder().comparedToSingleStore(5.0).percentage(33.33).build();

    var result = mapper.toShoppingSavings(domain);

    assertThat(result).isNotNull();
    assertThat(result.getComparedToSingleStore()).isEqualTo(5.0);
    assertThat(result.getPercentage()).isEqualTo(33.33);
  }

  @Test
  void shouldReturnNullForNullSavings() {
    assertThat(mapper.toShoppingSavings(null)).isNull();
  }
}
