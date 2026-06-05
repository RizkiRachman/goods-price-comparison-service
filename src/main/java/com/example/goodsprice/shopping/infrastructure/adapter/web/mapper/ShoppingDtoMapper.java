package com.example.goodsprice.shopping.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.ShoppingItem;
import com.example.goodsprice.api.model.ShoppingSavings;
import com.example.goodsprice.api.model.StoreVisit;
import com.example.goodsprice.shopping.application.domain.model.ShoppingItemDomain;
import com.example.goodsprice.shopping.application.domain.model.ShoppingSavingsDomain;
import com.example.goodsprice.shopping.application.domain.model.StoreVisitDomain;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingDtoMapper {

  StoreVisit toStoreVisit(StoreVisitDomain domain);

  default List<StoreVisit> toStoreVisits(List<StoreVisitDomain> domains) {
    if (Objects.isNull(domains)) return List.of();
    return domains.stream().map(this::toStoreVisit).toList();
  }

  ShoppingItem toShoppingItem(ShoppingItemDomain domain);

  default List<ShoppingItem> toShoppingItems(List<ShoppingItemDomain> domains) {
    if (Objects.isNull(domains)) return List.of();
    return domains.stream().map(this::toShoppingItem).toList();
  }

  ShoppingSavings toShoppingSavings(ShoppingSavingsDomain domain);
}
