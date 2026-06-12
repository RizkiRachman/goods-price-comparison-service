package com.example.goodsprice.price.application.domain.service;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.dto.PageResponse;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.common.util.CollectorUtils;
import com.example.goodsprice.common.util.ObjectUtils;
import com.example.goodsprice.price.application.domain.model.PriceCreateItem;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.price.application.port.in.dto.PriceCriteria;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PriceService extends AbstractGenericService<PriceDomain, Long> implements PriceInPort {

  private final PriceRepositoryPort priceRepository;
  private final ProductInPort productInPort;

  public PriceService(PriceRepositoryPort priceRepository, ProductInPort productInPort) {
    super("Price", ErrorCodes.PRICE_NOT_FOUND);
    this.priceRepository = priceRepository;
    this.productInPort = productInPort;
  }

  @Override
  protected GenericRepositoryPort<PriceDomain, Long> getRepository() {
    return priceRepository;
  }

  @Override
  @Transactional
  @ActivityLog
  public PriceDomain create(PriceDomain domain) {
    var priceRecord = save(domain);

    productInPort.updateLastPriceUpdate(domain.getProductId(), LocalDateTime.now());

    log.info(
        "Price created: product={}, store={}, price={}",
        domain.getProductId(),
        domain.getStoreId(),
        domain.getPrice());
    return priceRecord;
  }

  @Override
  public List<PriceDomain> searchByProduct(Long productId, LocalDate startDate, LocalDate endDate) {
    if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
      return priceRepository.findByProductIdAndDateRange(productId, startDate, endDate);
    }
    return priceRepository.findByProductId(productId);
  }

  @Override
  public PageResponse<PriceDomain> searchByProduct(PriceCriteria criteria) {
    return priceRepository.findByProductIdWithFilters(criteria);
  }

  @Override
  public PriceDomain findCheapestByProduct(Long productId) {
    var prices = priceRepository.findCheapestByProductId(productId);
    return prices.isEmpty() ? null : prices.getFirst();
  }

  @Override
  public Map<Long, PriceDomain> findCheapestByProducts(List<Long> productIds) {
    return priceRepository.findCheapestByProductIds(productIds).stream()
        .collect(CollectorUtils.toIdentityMap(PriceDomain::getProductId, (a, b) -> a));
  }

  @Override
  public List<PriceDomain> findAllByProductIds(List<Long> productIds) {
    return priceRepository.findAllByProductIds(productIds);
  }

  @Override
  @Transactional
  @ActivityLog
  public void createBatch(List<PriceCreateItem> items) {
    if (Objects.isNull(items) || items.isEmpty()) return;

    var prices =
        items.stream()
            .filter(item -> Objects.nonNull(item.productId()) && Objects.nonNull(item.storeId()))
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
  public PriceDomain update(Long id, PriceDomain domain) {
    var existing = findById(id);

    existing.setPrice(ObjectUtils.defaultIfNull(domain.getPrice(), existing.getPrice()));
    existing.setUnitPrice(
        ObjectUtils.defaultIfNull(domain.getUnitPrice(), existing.getUnitPrice()));
    existing.setDateRecorded(
        ObjectUtils.defaultIfNull(domain.getDateRecorded(), existing.getDateRecorded()));
    existing.setIsPromo(ObjectUtils.defaultIfNull(domain.getIsPromo(), Boolean.FALSE));

    existing = save(existing);

    productInPort.updateLastPriceUpdate(existing.getProductId(), LocalDateTime.now());

    log.info("Price updated: id={}, price={}", id, existing.getPrice());
    return existing;
  }
}
