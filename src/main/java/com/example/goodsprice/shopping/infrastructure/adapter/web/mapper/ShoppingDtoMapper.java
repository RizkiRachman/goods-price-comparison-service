package com.example.goodsprice.shopping.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.ShoppingItem;
import com.example.goodsprice.api.model.ShoppingSavings;
import com.example.goodsprice.api.model.StoreVisit;
import com.example.goodsprice.shopping.application.domain.model.ShoppingItemDomain;
import com.example.goodsprice.shopping.application.domain.model.ShoppingSavingsDomain;
import com.example.goodsprice.shopping.application.domain.model.StoreVisitDomain;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ShoppingDtoMapper {

  public StoreVisit toStoreVisit(StoreVisitDomain domain) {
    var result = new StoreVisit();
    result.setStoreId(domain.getStoreId());
    result.setStoreName(domain.getStoreName());
    result.setStoreLocation(domain.getStoreLocation());
    result.setItems(toShoppingItems(domain.getItems()));
    result.setSubtotal(domain.getSubtotal());
    result.setEstimatedTime(domain.getEstimatedTime());
    return result;
  }

  public List<StoreVisit> toStoreVisits(List<StoreVisitDomain> domains) {
    if (Objects.isNull(domains)) return List.of();
    return domains.stream().map(this::toStoreVisit).toList();
  }

  public ShoppingItem toShoppingItem(ShoppingItemDomain domain) {
    var result = new ShoppingItem();
    result.setProductName(domain.getProductName());
    result.setPrice(domain.getPrice());
    result.setQuantity(domain.getQuantity());
    return result;
  }

  public List<ShoppingItem> toShoppingItems(List<ShoppingItemDomain> domains) {
    if (Objects.isNull(domains)) return List.of();
    return domains.stream().map(this::toShoppingItem).toList();
  }

  public ShoppingSavings toShoppingSavings(ShoppingSavingsDomain domain) {
    if (Objects.isNull(domain)) return null;
    var result = new ShoppingSavings();
    result.setComparedToSingleStore(domain.getComparedToSingleStore());
    result.setPercentage(domain.getPercentage());
    return result;
  }
}
