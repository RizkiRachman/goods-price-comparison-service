package com.example.goodsprice.price.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.dto.PageRequestDto;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.exception.NotFoundException;
import com.example.goodsprice.price.application.domain.model.PriceCreateItem;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService implements PriceInPort {

  private final PriceRepositoryPort priceRepository;
  private final ProductInPort productInPort;

  @Override
  @Transactional
  @ActivityLog
  public PriceDomain create(
      Long productId,
      Long storeId,
      Double price,
      Double unitPrice,
      LocalDate dateRecorded,
      Boolean isPromo) {
    var priceRecord =
        PriceDomain.builder()
            .productId(productId)
            .storeId(storeId)
            .price(price)
            .unitPrice(unitPrice)
            .dateRecorded(dateRecorded)
            .isPromo(isPromo)
            .build();
    priceRecord = priceRepository.save(priceRecord);

    productInPort.updateLastPriceUpdate(productId, LocalDateTime.now());

    log.info("Price created: product={}, store={}, price={}", productId, storeId, price);
    return priceRecord;
  }

  @Override
  public PriceDomain findById(Long id) {
    var price = priceRepository.findById(id);
    if (Objects.isNull(price)) throw NotFoundException.price(id);
    return price;
  }

  @Override
  public List<PriceDomain> searchByProduct(Long productId, LocalDate startDate, LocalDate endDate) {
    if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
      return priceRepository.findByProductIdAndDateRange(productId, startDate, endDate);
    }
    return priceRepository.findByProductId(productId);
  }

  @Override
  public PageResponse<PriceDomain> searchByProduct(
      Long productId,
      LocalDate startDate,
      LocalDate endDate,
      Long storeId,
      Boolean isPromo,
      PageRequestDto pageRequest) {
    return priceRepository.findByProductIdWithFilters(
        productId, startDate, endDate, storeId, isPromo, pageRequest);
  }

  @Override
  public PriceDomain findCheapestByProduct(Long productId) {
    var prices = priceRepository.findCheapestByProductId(productId);
    return prices.isEmpty() ? null : prices.get(0);
  }

  @Override
  public Map<Long, PriceDomain> findCheapestByProducts(List<Long> productIds) {
    return priceRepository.findCheapestByProductIds(productIds).stream()
        .collect(Collectors.toMap(PriceDomain::getProductId, Function.identity(), (a, b) -> a));
  }

  @Override
  public List<PriceDomain> findAllByProductIds(List<Long> productIds) {
    return priceRepository.findAllByProductIds(productIds);
  }

  @Override
  @Transactional
  @ActivityLog
  public void createBatch(List<PriceCreateItem> items) {
    if (items == null || items.isEmpty()) return;

    var prices =
        items.stream()
            .filter(item -> item.productId() != null && item.storeId() != null)
            .map(
                item ->
                    PriceDomain.builder()
                        .productId(item.productId())
                        .storeId(item.storeId())
                        .price(item.totalPrice())
                        .unitPrice(item.unitPrice())
                        .dateRecorded(item.dateRecorded())
                        .isPromo(item.isPromo())
                        .build())
            .toList();

    if (prices.isEmpty()) return;

    priceRepository.saveAll(prices);

    // Update last price update timestamp for all affected products
    var productIds =
        prices.stream()
            .map(PriceDomain::getProductId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    var now = LocalDateTime.now();
    productIds.forEach(id -> productInPort.updateLastPriceUpdate(id, now));

    log.info("Created {} prices in batch", prices.size());
  }

  @Override
  @Transactional
  @ActivityLog
  public void deleteById(Long id) {
    var price = priceRepository.findById(id);
    if (Objects.isNull(price)) throw NotFoundException.price(id);
    priceRepository.deleteById(id);
    log.info("Price deleted: id={}", id);
  }

  @Override
  @Transactional
  @ActivityLog
  public PriceDomain update(
      Long id, Double price, Double unitPrice, LocalDate dateRecorded, Boolean isPromo) {
    var existing = priceRepository.findById(id);
    if (Objects.isNull(existing)) throw NotFoundException.price(id);

    existing.setPrice(price);
    existing.setUnitPrice(unitPrice);
    existing.setDateRecorded(dateRecorded);
    existing.setIsPromo(isPromo);

    existing = priceRepository.save(existing);

    productInPort.updateLastPriceUpdate(existing.getProductId(), LocalDateTime.now());

    log.info("Price updated: id={}, price={}", id, existing.getPrice());
    return existing;
  }
}
