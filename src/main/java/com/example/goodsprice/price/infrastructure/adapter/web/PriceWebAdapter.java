package com.example.goodsprice.price.infrastructure.adapter.web;

import com.example.goodsprice.api.model.CheapestPrice;
import com.example.goodsprice.api.model.CreatePriceRecordRequest;
import com.example.goodsprice.api.model.DateRange;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.PriceRecord;
import com.example.goodsprice.api.model.PriceRecordListResponse;
import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.api.model.PriceSearchResponse;
import com.example.goodsprice.api.model.PriceSearchResponseV2;
import com.example.goodsprice.api.model.UpdatePriceRecordRequest;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.common.util.StoreMapBuilder;
import com.example.goodsprice.common.web.AbstractCrudWebAdapter;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.price.application.port.in.dto.PriceCriteria;
import com.example.goodsprice.price.infrastructure.adapter.web.mapper.PriceDtoMapper;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.in.StoreInPort;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceWebAdapter extends AbstractCrudWebAdapter {

  private final PriceInPort priceInPort;
  private final ProductInPort productInPort;
  private final StoreInPort storeInPort;
  private final PriceDtoMapper mapper;

  public PriceRecord createPriceRecord(Long productId, CreatePriceRecordRequest request) {
    var dateRecorded =
        ObjectUtils.getOrNull(request.getDateRecorded(), OffsetDateTime::toLocalDate);
    var domain =
        PriceDomain.builder()
            .productId(productId)
            .storeId(request.getStoreId())
            .price(request.getPrice())
            .unitPrice(request.getUnitPrice())
            .dateRecorded(dateRecorded)
            .isPromo(request.getIsPromo())
            .build();
    var price = priceInPort.create(domain);
    var store = storeInPort.findById(price.getStoreId());
    return mapper.toPriceRecord(price, store);
  }

  public PriceRecord getPriceRecord(Long id) {
    var price = priceInPort.findById(id);
    var store = storeInPort.findById(price.getStoreId());
    return mapper.toPriceRecord(price, store);
  }

  public void deletePriceRecord(Long id) {
    priceInPort.deleteById(id);
  }

  public PriceRecordListResponse listProductPrices(
      Long productId,
      Long storeId,
      OffsetDateTime startDate,
      OffsetDateTime endDate,
      Boolean isPromo,
      EntityStatus status,
      Integer page,
      Integer size,
      String sortBy,
      String sortDirection) {
    var params = resolvePagination(page, size, sortBy, sortDirection, "dateRecorded", "desc");
    var pageRequest =
        new PageRequestDto(params.page(), params.size(), params.sortBy(), params.sortOrder());
    var criteria =
        new PriceCriteria(
            productId,
            ObjectUtils.getOrNull(startDate, OffsetDateTime::toLocalDate),
            ObjectUtils.getOrNull(endDate, OffsetDateTime::toLocalDate),
            storeId,
            isPromo,
            pageRequest);

    var pageResponse = priceInPort.searchByProduct(criteria);

    var storeMap =
        StoreMapBuilder.buildFromIds(
            pageResponse.content().stream()
                .map(PriceDomain::getStoreId)
                .filter(Objects::nonNull)
                .distinct()
                .toList(),
            storeInPort::findAllById,
            StoreDomain::getId);

    return buildCompleteListResponse(
        pageResponse,
        p -> mapper.toPriceRecord(p, storeMap.get(p.getStoreId())),
        (data, pagination) -> {
          var res = new PriceRecordListResponse();
          res.setData(data);
          res.setPagination(pagination);
          return res;
        });
  }

  public PriceRecord updatePriceRecord(Long id, UpdatePriceRecordRequest request) {
    var dateRecorded =
        ObjectUtils.getOrNull(request.getDateRecorded(), OffsetDateTime::toLocalDate);
    var unitPrice = ObjectUtils.getOrNull(request.getUnitPrice(), u -> u.orElse(null));
    var domain =
        PriceDomain.builder()
            .price(request.getPrice())
            .unitPrice(unitPrice)
            .dateRecorded(dateRecorded)
            .isPromo(request.getIsPromo())
            .build();
    var price = priceInPort.update(id, domain);
    var store = storeInPort.findById(price.getStoreId());
    return mapper.toPriceRecord(price, store);
  }

  public PriceSearchResponse search(PriceSearchRequest request) {
    var ctx = resolveRequest(request.getProductName(), request.getDateRange());
    return doSearchV1(ctx);
  }

  public PriceSearchResponseV2 searchV2(PriceSearchRequestV2 request) {
    var ctx = resolveRequest(request.getProductName(), request.getDateRange());
    return doSearchV2(ctx);
  }

  private PriceSearchResponse doSearchV1(SearchContext ctx) {
    var storeMap =
        StoreMapBuilder.buildFromIds(
            ctx.prices.stream()
                .map(PriceDomain::getStoreId)
                .filter(Objects::nonNull)
                .distinct()
                .toList(),
            storeInPort::findAllById,
            StoreDomain::getId);

    var results =
        ctx.prices.stream().map(p -> mapper.toResult(p, storeMap.get(p.getStoreId()))).toList();
    var cheapest = buildCheapest(ctx.product.getId(), ctx.prices, storeMap, true);
    var response = new PriceSearchResponse();
    response.productName(ctx.product.getName());
    response.setResults(results);
    response.setCheapest(cheapest);
    return response;
  }

  private PriceSearchResponseV2 doSearchV2(SearchContext ctx) {
    var storeMap =
        StoreMapBuilder.buildFromIds(
            ctx.prices.stream()
                .map(PriceDomain::getStoreId)
                .filter(Objects::nonNull)
                .distinct()
                .toList(),
            storeInPort::findAllById,
            StoreDomain::getId);

    var results =
        ctx.prices.stream().map(p -> mapper.toResultV2(p, storeMap.get(p.getStoreId()))).toList();
    var cheapest = buildCheapest(ctx.product.getId(), ctx.prices, storeMap, false);
    var response = new PriceSearchResponseV2();
    response.productName(ctx.product.getName());
    response.setResults(results);
    response.setCheapest(cheapest);
    return response;
  }

  private record SearchContext(ProductDomain product, List<PriceDomain> prices) {}

  private SearchContext resolveRequest(String productName, DateRange dateRange) {
    if (Objects.isNull(productName)) {
      throw new IllegalArgumentException("Product name must not be null");
    }
    var products = productInPort.searchByName(productName);
    if (products.isEmpty()) {
      throw NotFoundException.product(productName);
    }
    var product = products.getFirst();
    var fromDate = ObjectUtils.getOrNull(dateRange, DateRange::getFrom);
    var toDate = ObjectUtils.getOrNull(dateRange, DateRange::getTo);
    var prices = priceInPort.searchByProduct(product.getId(), fromDate, toDate);
    return new SearchContext(product, prices);
  }

  private CheapestPrice buildCheapest(
      Long productId,
      List<PriceDomain> prices,
      Map<Long, StoreDomain> storeMap,
      boolean includeSavings) {
    var cheapest = priceInPort.findCheapestByProduct(productId);
    if (Objects.isNull(cheapest)) return null;

    var store = storeMap.get(cheapest.getStoreId());
    if (Objects.isNull(store)) {
      store = storeInPort.findById(cheapest.getStoreId());
    }
    var result = mapper.toCheapestPrice(cheapest, store);

    if (includeSavings && prices.size() > 1) {
      var avg = prices.stream().mapToDouble(PriceDomain::getPrice).average().orElse(0);
      result.setSavings(avg - cheapest.getPrice());
    }
    return result;
  }
}
