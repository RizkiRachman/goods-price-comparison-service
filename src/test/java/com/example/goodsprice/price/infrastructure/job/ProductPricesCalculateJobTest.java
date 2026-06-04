package com.example.goodsprice.price.infrastructure.job;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.example.goodsprice.admin.job.JobExecutor;
import com.example.goodsprice.admin.job.JobRegistry;
import com.example.goodsprice.price.application.domain.service.PriceSummaryBatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductPricesCalculateJobTest {

  @Mock private JobRegistry jobRegistry;
  @Mock private PriceSummaryBatchService batchService;

  @InjectMocks private ProductPricesCalculateJob job;

  @Captor private ArgumentCaptor<JobExecutor> executorCaptor;

  @Test
  @DisplayName("Should register job with correct name")
  void shouldRegisterJobWithCorrectName() {
    job.register();

    verify(jobRegistry).register(eq("product-prices-calculate"), executorCaptor.capture());
  }

  @Test
  @DisplayName("Should execute batch update when job runs")
  void shouldExecuteBatchUpdateWhenJobRuns() {
    job.register();

    verify(jobRegistry).register(eq("product-prices-calculate"), executorCaptor.capture());

    JobExecutor executor = executorCaptor.getValue();
    executor.run();

    verify(batchService).updateSummaries();
  }
}
