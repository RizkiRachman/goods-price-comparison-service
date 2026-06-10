package com.example.goodsprice.product.infrastructure.adapter.web;

import static com.example.goodsprice.common.util.JsonNullableUtils.resolveNullable;

import com.example.goodsprice.api.model.CreateProductRequest;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.ListProducts200Response;
import com.example.goodsprice.api.model.Product;
import com.example.goodsprice.api.model.ProductListResponse;
import com.example.goodsprice.api.model.ProductTrendResponse;
import com.example.goodsprice.api.model.UpdateProductRequest;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.util.CollectorUtils;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.domain.model.ProductSearchCriteria;
import com.example.goodsprice.product.application.port.in.PriceSummaryInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.product.infrastructure.adapter.web.mapper.ProductDtoMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductWebAdapter {

  private final ProductInPort productInPort;
  private final PriceSummaryInPort priceSummaryInPort;
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

    var pageResponse = productInPort.search(criteria);

    Map<Long, ProductPriceSummary> summaryMap = buildSummaryMap(includePrice, pageResponse);

    var response = new ProductListResponse();
    response.setData(
        pageResponse.content().stream()
            .map(product -> mapper.toApiProduct(product, summaryMap.get(product.getId())))
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

  private Map<Long, ProductPriceSummary> buildSummaryMap(
      Boolean includePrice, PageResponse<ProductDomain> pageResponse) {
    if (!Boolean.TRUE.equals(includePrice) || pageResponse.content().isEmpty()) {
      return Collections.emptyMap();
    }

    Set<Long> productIds =
        pageResponse.content().stream()
            .map(ProductDomain::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    var summaries = priceSummaryInPort.findByProductIds(productIds);
    if (Objects.isNull(summaries)) {
      return Collections.emptyMap();
    }

    return summaries.stream()
        .filter(s -> Objects.nonNull(s.getProductId()))
        .collect(CollectorUtils.toIdentityMap(ProductPriceSummary::getProductId));
  }
}
