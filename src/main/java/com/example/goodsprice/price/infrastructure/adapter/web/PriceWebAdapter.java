package com.example.goodsprice.price.infrastructure.adapter.web;

import com.example.goodsprice.api.model.CheapestPrice;
import com.example.goodsprice.api.model.CreatePriceRecordRequest;
import com.example.goodsprice.api.model.DateRange;
import com.example.goodsprice.api.model.EntityStatus;
import com.example.goodsprice.api.model.Pagination;
import com.example.goodsprice.api.model.PriceRecord;
import com.example.goodsprice.api.model.PriceRecordListResponse;
import com.example.goodsprice.api.model.PriceSearchRequest;
import com.example.goodsprice.api.model.PriceSearchRequestV2;
import com.example.goodsprice.api.model.PriceSearchResponse;
import com.example.goodsprice.api.model.PriceSearchResponseV2;
import com.example.goodsprice.api.model.UpdatePriceRecordRequest;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.price.infrastructure.adapter.web.mapper.PriceDtoMapper;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceWebAdapter {

  private final PriceInPort priceInPort;
  private final ProductInPort productInPort;
  private final StoreRepositoryPort storeRepository;
  private final PriceDtoMapper mapper;

  public PriceRecord createPriceRecord(Long productId, CreatePriceRecordRequest request) {
    var dateRecorded =
        ObjectUtils.getOrNull(request.getDateRecorded(), OffsetDateTime::toLocalDate);
    var price =
        priceInPort.create(
            productId,
            request.getStoreId(),
            request.getPrice(),
            request.getUnitPrice(),
            dateRecorded,
            request.getIsPromo());
    var store = storeRepository.findById(price.getStoreId());
    return mapper.toPriceRecord(price, store);
  }

  public PriceRecord getPriceRecord(Long id) {
    var price = priceInPort.findById(id);
    var store = storeRepository.findById(price.getStoreId());
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
    var fromDate = ObjectUtils.getOrNull(startDate, OffsetDateTime::toLocalDate);
    var toDate = ObjectUtils.getOrNull(endDate, OffsetDateTime::toLocalDate);
    var prices = priceInPort.searchByProduct(productId, fromDate, toDate);

    var filteredPrices =
        prices.stream()
            .filter(p -> Objects.isNull(storeId) || storeId.equals(p.getStoreId()))
            .filter(p -> Objects.isNull(isPromo) || isPromo.equals(p.getIsPromo()))
            .toList();

    var actualPage = ObjectUtils.getOrDefault(page, p -> p, 0);
    var actualSize = ObjectUtils.getOrDefault(size, s -> s, 20);
    var totalItems = filteredPrices.size();

    var pagination = new Pagination();
    pagination.setPage(Math.max(actualPage, 1));
    pagination.setPageSize(actualSize);
    pagination.setTotalItems(totalItems);
    var totalPages = (int) Math.ceil((double) totalItems / actualSize);
    pagination.setTotalPages(totalPages);
    pagination.setHasNext(actualPage < totalPages);
    pagination.setHasPrevious(actualPage > 1);

    var storeMap = fetchStoresMap(filteredPrices);
    var records =
        filteredPrices.stream()
            .map(p -> mapper.toPriceRecord(p, storeMap.get(p.getStoreId())))
            .toList();

    var response = new PriceRecordListResponse();
    response.setData(records);
    response.setPagination(pagination);
    return response;
  }

  public PriceRecord updatePriceRecord(Long id, UpdatePriceRecordRequest request) {
    var dateRecorded =
        ObjectUtils.getOrNull(request.getDateRecorded(), OffsetDateTime::toLocalDate);
    var unitPrice = ObjectUtils.getOrNull(request.getUnitPrice(), u -> u.orElse(null));
    var price =
        priceInPort.update(id, request.getPrice(), unitPrice, dateRecorded, request.getIsPromo());
    var store = storeRepository.findById(price.getStoreId());
    return mapper.toPriceRecord(price, store);
  }

  public PriceSearchResponse search(PriceSearchRequest request) {
    var response = new PriceSearchResponse();
    var ctx = resolveRequest(request.getProductName(), request.getDateRange());
    if (Objects.isNull(ctx)) return response;

    response.productName(ctx.product.getName());
    var storeMap = fetchStoresMap(ctx.prices);
    response.setResults(
        ctx.prices.stream().map(p -> mapper.toResult(p, storeMap.get(p.getStoreId()))).toList());
    response.setCheapest(buildCheapest(ctx.product.getId(), ctx.prices, storeMap, true));

    return response;
  }

  public PriceSearchResponseV2 searchV2(PriceSearchRequestV2 request) {
    var response = new PriceSearchResponseV2();
    var ctx = resolveRequest(request.getProductName(), request.getDateRange());
    if (Objects.isNull(ctx)) return response;

    response.productName(ctx.product.getName());
    var storeMap = fetchStoresMap(ctx.prices);
    response.setResults(
        ctx.prices.stream().map(p -> mapper.toResultV2(p, storeMap.get(p.getStoreId()))).toList());
    response.setCheapest(buildCheapest(ctx.product.getId(), ctx.prices, storeMap, false));

    return response;
  }

  private record SearchContext(ProductDomain product, List<PriceDomain> prices) {}

  private SearchContext resolveRequest(String productName, DateRange dateRange) {
    if (Objects.isNull(productName)) return null;
    var product = productInPort.findByName(productName);
    var fromDate = ObjectUtils.getOrNull(dateRange, DateRange::getFrom);
    var toDate = ObjectUtils.getOrNull(dateRange, DateRange::getTo);
    var prices = priceInPort.searchByProduct(product.getId(), fromDate, toDate);
    return new SearchContext(product, prices);
  }

  private Map<Long, StoreDomain> fetchStoresMap(List<PriceDomain> prices) {
    var storeIds =
        prices.stream().map(PriceDomain::getStoreId).filter(Objects::nonNull).distinct().toList();
    if (storeIds.isEmpty()) return Map.of();
    return storeRepository.findAllById(storeIds).stream()
        .collect(Collectors.toMap(StoreDomain::getId, Function.identity()));
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
      store = storeRepository.findById(cheapest.getStoreId());
    }
    var result = mapper.toCheapestPrice(cheapest, store);

    if (includeSavings && prices.size() > 1) {
      var avg = prices.stream().mapToDouble(PriceDomain::getPrice).average().orElse(0);
      result.setSavings(avg - cheapest.getPrice());
    }
    return result;
  }
}
