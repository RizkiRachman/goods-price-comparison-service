package com.example.goodsprice.price.application.domain.service;

import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.out.ProductRepositoryPort;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceSummaryBatchService {

  private final PriceBatchProcessor priceBatchProcessor;
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

      priceBatchProcessor.processProductBatch(productsNeedingUpdate);
      totalProcessed += totalProducts;

    } while (totalProducts == batchSize);

    log.info(
        "Price summary batch update completed. Processed {} products in {}",
        totalProcessed,
        Duration.between(startTime, LocalDateTime.now()));
  }
}
