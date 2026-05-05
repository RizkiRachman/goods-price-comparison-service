package com.example.goodsprice.price.infrastructure.job;

import com.example.goodsprice.admin.job.JobRegistry;
import com.example.goodsprice.price.application.domain.service.PriceSummaryBatchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductPricesCalculateJob {

  private static final String JOB_NAME = "product-prices-calculate";

  private final JobRegistry jobRegistry;
  private final PriceSummaryBatchService batchService;

  @PostConstruct
  void register() {
    jobRegistry.register(JOB_NAME, () -> {
      log.info("Executing job: {}", JOB_NAME);
      batchService.updateSummaries();
    });
  }
}
