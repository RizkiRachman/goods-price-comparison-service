package com.example.goodsprice.price.infrastructure.scheduler;

import com.example.goodsprice.price.application.domain.service.PriceSummaryBatchService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "price.summary.batch",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class PriceSummaryBatchScheduler {

  private final PriceSummaryBatchService batchService;

  @Value("${price.summary.batch.interval:15}")
  private int intervalMinutes;

  @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
  public void scheduledSummaryUpdate() {
    log.info("Starting scheduled price summary update (interval: {} minutes)", intervalMinutes);
    try {
      batchService.updateSummaries();
    } catch (Exception e) {
      log.error("Price summary batch update failed: {}", e.getMessage(), e);
    }
  }
}
