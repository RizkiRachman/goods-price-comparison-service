package com.example.goodsprice.price.application.domain.service;

import com.example.goodsprice.common.constant.UnitConstants;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.price.application.domain.model.ProductPriceSummary;
import com.example.goodsprice.price.application.port.out.PriceRepositoryPort;
import com.example.goodsprice.price.application.port.out.PriceSummaryRepositoryPort;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceSummaryBatchService {

  private final PriceRepositoryPort priceRepository;
  private final PriceSummaryRepositoryPort priceSummaryRepository;
  private final ProductRepositoryPort productRepository;

  @Value("${price.summary.batch.batch-size:100}")
  private int batchSize;

  public void updateSummaries() {
    log.info("Starting price summary batch update");

    LocalDateTime startTime = LocalDateTime.now();
    int totalProcessed = 0;
    int totalProducts;

    do {
      List<ProductDomain> productsNeedingUpdate =
          productRepository.findProductsNeedingSummaryUpdate(batchSize);

      if (productsNeedingUpdate.isEmpty()) {
        log.info("No more products need summary updates");
        break;
      }

      totalProducts = productsNeedingUpdate.size();
      log.info("Processing batch of {} products", totalProducts);

      processProductBatch(productsNeedingUpdate);
      totalProcessed += totalProducts;

    } while (totalProducts == batchSize);

    log.info(
        "Price summary batch update completed. Processed {} products in {}",
        totalProcessed,
        java.time.Duration.between(startTime, LocalDateTime.now()));
  }

  @Transactional
  public void processProductBatch(List<ProductDomain> products) {
    List<ProductPriceSummary> summariesToSave = new ArrayList<>();
    LocalDateTime calculationTime = LocalDateTime.now();

    // Batch-load all prices in one query instead of N+1
    var productIds = products.stream().map(ProductDomain::getId).filter(Objects::nonNull).toList();
    var allPrices = priceRepository.findAllByProductIds(productIds);
    var pricesByProductId =
        allPrices.stream()
            .collect(java.util.stream.Collectors.groupingBy(PriceDomain::getProductId));

    for (ProductDomain product : products) {
      try {
        ProductPriceSummary summary =
            calculateSummaryForProduct(product, calculationTime, pricesByProductId);
        summariesToSave.add(summary);
      } catch (Exception e) {
        log.error(
            "Failed to calculate summary for product {}: {}", product.getId(), e.getMessage());
      }
    }

    if (!summariesToSave.isEmpty()) {
      priceSummaryRepository.saveAll(summariesToSave);
      log.info("Saved {} price summaries", summariesToSave.size());
    }

    productRepository.updateSummaryLastCalculated(productIds, calculationTime);
  }

  private ProductPriceSummary calculateSummaryForProduct(
      ProductDomain product,
      LocalDateTime calculationTime,
      Map<Long, List<PriceDomain>> pricesByProductId) {

    Long productId = product.getId();

    List<PriceDomain> allPrices = pricesByProductId.getOrDefault(productId, List.of());

    if (allPrices.isEmpty()) {
      return buildEmptySummary(productId, calculationTime);
    }

    PriceStatistics stats = calculateStatistics(allPrices, product.getUnit());

    LocalDate lastPriceDate =
        allPrices.stream()
            .map(PriceDomain::getDateRecorded)
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo)
            .orElse(null);

    return ProductPriceSummary.builder()
        .productId(productId)
        .avgPrice(stats.avgPrice)
        .minPrice(stats.minPrice)
        .maxPrice(stats.maxPrice)
        .storeCount(stats.storeCount)
        .priceCount(stats.priceCount)
        .lastCalculatedAt(calculationTime)
        .lastPriceDate(lastPriceDate)
        .build();
  }

  private ProductPriceSummary buildEmptySummary(Long productId, LocalDateTime calculationTime) {
    return ProductPriceSummary.builder()
        .productId(productId)
        .lastCalculatedAt(calculationTime)
        .build();
  }

  private PriceStatistics calculateStatistics(List<PriceDomain> prices, String unit) {
    if (prices.isEmpty()) {
      return PriceStatistics.empty();
    }

    boolean useUnitPrice = UnitConstants.isWeight(unit);

    List<Double> validPrices =
        prices.stream()
            .map(p -> useUnitPrice ? p.getUnitPrice() : p.getPrice())
            .filter(Objects::nonNull)
            .filter(p -> p > 0)
            .toList();

    if (validPrices.isEmpty()) {
      return PriceStatistics.empty();
    }

    var stats = validPrices.stream().mapToDouble(Double::doubleValue).summaryStatistics();
    double min = stats.getMin();
    double max = stats.getMax();
    double avg = stats.getAverage();

    long uniqueStores =
        prices.stream().map(PriceDomain::getStoreId).filter(Objects::nonNull).distinct().count();

    return new PriceStatistics(
        toBigDecimal(avg), toBigDecimal(min), toBigDecimal(max), (int) uniqueStores, prices.size());
  }

  private BigDecimal toBigDecimal(Double value) {
    if (Objects.isNull(value)) {
      return null;
    }
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }

  private record PriceStatistics(
      BigDecimal avgPrice,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      int storeCount,
      int priceCount) {

    static PriceStatistics empty() {
      return new PriceStatistics(null, null, null, 0, 0);
    }
  }
}
