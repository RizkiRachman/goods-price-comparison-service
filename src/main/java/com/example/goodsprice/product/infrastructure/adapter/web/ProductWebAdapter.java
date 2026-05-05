package com.example.goodsprice.product.infrastructure.adapter.web;

import com.example.goodsprice.api.model.*;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.product.infrastructure.adapter.web.mapper.ProductDtoMapper;
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductWebAdapter {

  private final ProductInPort productInPort;
  private final ProductDtoMapper mapper;

  public Product create(CreateProductRequest request) {
    var domain =
        productInPort.create(
            request.getName(), request.getCategory(), request.getBrand(), request.getUnit());
    return mapper.toApiProduct(domain);
  }

  public Product findById(Long id) {
    var domain = productInPort.findById(id);
    return mapper.toApiProduct(domain);
  }

  public ListProducts200Response list(
      Integer page,
      Integer pageSize,
      String sortBy,
      String sortOrder,
      String search,
      EntityStatus status,
      String category,
      String brand,
      Boolean includePrice,
      String storeId) {

    var criteria =
        ProductSearchCriteria.builder()
            .search(search)
            .category(category)
            .brand(brand)
            .status(ObjectUtils.getOrNull(status, EntityStatus::getValue))
            .sortBy(sortBy)
            .sortDirection(sortOrder)
            .page(page)
            .size(pageSize)
            .storeId(storeId)
            .build();

    boolean shouldIncludePrice = Boolean.TRUE.equals(includePrice);
    var pageResponse = productInPort.search(criteria, shouldIncludePrice);

    var response = new ProductListResponse();
    response.setData(
        pageResponse.content().stream()
            .map(product -> mapper.toApiProduct(product, shouldIncludePrice))
            .toList());
    response.setPagination(pageResponse.toPagination());
    return response;
  }

  public Product update(Long id, UpdateProductRequest request) {
    var domain =
        productInPort.update(
            id,
            request.getName(),
            resolveNullable(request.getCategory()),
            resolveNullable(request.getBrand()),
            resolveNullable(request.getUnit()));
    return mapper.toApiProduct(domain);
  }

  public void delete(Long id) {
    productInPort.deleteById(id);
  }

  public ProductTrendResponse getTrend(
      Long productId, LocalDate startDate, LocalDate endDate, String granularity) {
    var domain = productInPort.findById(productId);
    var response = new ProductTrendResponse();
    response.setProductId(domain.getId());
    response.setProductName(domain.getName());
    return response;
  }

  private <T> T resolveNullable(JsonNullable<T> nullable) {
    if (Objects.isNull(nullable)) return null;
    return nullable.orElse(null);
  }
}
