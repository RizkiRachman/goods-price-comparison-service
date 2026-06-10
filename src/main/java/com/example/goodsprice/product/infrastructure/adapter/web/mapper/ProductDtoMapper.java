package com.example.goodsprice.product.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.Product;
import com.example.goodsprice.api.model.ProductDetail;
import com.example.goodsprice.api.model.ProductDetailPrice;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.web.mapper.DtoMapperSupport;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper implements DtoMapperSupport {

  public Product toApiProduct(ProductDomain domain) {
    return toApiProduct(domain, null);
  }

  public Product toApiProduct(ProductDomain domain, ProductPriceSummary summary) {
    return mapIfNotNull(
        domain,
        d -> {
          var result = new Product();
          result.setId(d.getId());
          result.setName(d.getName());
          result.setCategory(d.getCategory());
          result.setBrand(d.getBrand());
          result.setUnit(d.getUnit());
          result.setStatus(resolveStatusValue(d.getStatus()));

          if (Objects.nonNull(summary)) {
            var detail = new ProductDetail();
            var price = new ProductDetailPrice();
            price.setAvg(ObjectUtils.getOrNull(summary.getAvgPrice(), BigDecimal::doubleValue));
            price.setMin(ObjectUtils.getOrNull(summary.getMinPrice(), BigDecimal::doubleValue));
            price.setMax(ObjectUtils.getOrNull(summary.getMaxPrice(), BigDecimal::doubleValue));
            price.setUpdatedAt(JsonNullable.of(toOffsetDateTime(summary.getLastCalculatedAt())));
            detail.setPrice(price);
            result.setDetail(detail);
          }

          return result;
        });
  }

  private static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
    if (Objects.isNull(localDateTime)) {
      return null;
    }
    return localDateTime.atOffset(ZoneOffset.UTC);
  }
}
