package com.example.goodsprice.price.application.domain.service;

import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.exception.PriceNotFoundException;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService implements PriceInPort {

  private final PriceRepositoryPort priceRepository;
  private final ProductRepositoryPort productRepository;

  @Override
  @Transactional
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

    // Update product's lastPriceUpdate timestamp to trigger summary recalculation
    productRepository.updateLastPriceUpdate(productId, LocalDateTime.now());

    log.info("Price created: product={}, store={}, price={}", productId, storeId, price);
    return priceRecord;
  }

  @Override
  public PriceDomain findById(Long id) {
    var price = priceRepository.findById(id);
    if (Objects.isNull(price)) throw new PriceNotFoundException(id);
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
  public PriceDomain findCheapestByProduct(Long productId) {
    var prices = priceRepository.findCheapestByProductId(productId);
    return prices.isEmpty() ? null : prices.get(0);
  }

  @Override
  @Transactional
  public void deleteById(Long id) {
    var price = priceRepository.findById(id);
    if (Objects.isNull(price)) throw new PriceNotFoundException(id);
    priceRepository.deleteById(id);
    log.info("Price deleted: id={}", id);
  }

  @Override
  @Transactional
  public PriceDomain update(
      Long id, Double price, Double unitPrice, LocalDate dateRecorded, Boolean isPromo) {
    var existing = priceRepository.findById(id);
    if (Objects.isNull(existing)) throw new PriceNotFoundException(id);

    existing.setPrice(price);
    existing.setUnitPrice(unitPrice);
    existing.setDateRecorded(dateRecorded);
    existing.setIsPromo(isPromo);

    existing = priceRepository.save(existing);

    // Update product's lastPriceUpdate timestamp to trigger summary recalculation
    productRepository.updateLastPriceUpdate(existing.getProductId(), LocalDateTime.now());

    log.info("Price updated: id={}, price={}", id, existing.getPrice());
    return existing;
  }
}
