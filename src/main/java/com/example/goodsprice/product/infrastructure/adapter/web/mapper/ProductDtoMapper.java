package com.example.goodsprice.product.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Product;
import com.example.goodsprice.api.model.ProductDetail;
import com.example.goodsprice.api.model.ProductDetailPrice;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {

  public Product toApiProduct(ProductDomain domain) {
    return toApiProduct(domain, false);
  }

  public Product toApiProduct(ProductDomain domain, boolean includePrice) {
    if (Objects.isNull(domain)) return null;
    var result = new Product();
    result.setId(domain.getId());
    result.setName(domain.getName());
    result.setCategory(domain.getCategory());
    result.setBrand(domain.getBrand());
    result.setUnit(domain.getUnit());
    result.setStatus(ObjectUtils.getOrNull(domain.getStatus(), EntityStatus::fromValue));

    // Add price detail if requested and data exists
    if (includePrice && hasPriceData(domain)) {
      var detail = new ProductDetail();
      var price = new ProductDetailPrice();
      price.setAvg(ObjectUtils.getOrNull(domain.getAvgPrice(), BigDecimal::doubleValue));
      price.setMin(ObjectUtils.getOrNull(domain.getMinPrice(), BigDecimal::doubleValue));
      price.setMax(ObjectUtils.getOrNull(domain.getMaxPrice(), BigDecimal::doubleValue));
      price.setUpdatedAt(JsonNullable.of(toOffsetDateTime(domain.getPriceUpdatedAt())));
      detail.setPrice(price);
      result.setDetail(detail);
    }

    return result;
  }

  private OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
    if (Objects.isNull(localDateTime)) {
      return null;
    }
    return localDateTime.atOffset(ZoneOffset.UTC);
  }

  private boolean hasPriceData(ProductDomain domain) {
    return Objects.nonNull(domain.getAvgPrice())
        || Objects.nonNull(domain.getMinPrice())
        || Objects.nonNull(domain.getMaxPrice());
  }
}
