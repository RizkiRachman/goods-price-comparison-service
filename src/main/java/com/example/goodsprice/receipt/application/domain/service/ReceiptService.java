package com.example.goodsprice.receipt.application.domain.service;

import static com.example.goodsprice.common.constant.ErrorMessageConstants.ITEMS_NOT_EMPTY_MSG;

import com.example.goodsprice.activity.application.annotation.ActivityLog;
import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.repository.GenericRepositoryPort;
import com.example.goodsprice.common.service.AbstractGenericService;
import com.example.goodsprice.common.util.HashUtils;
import com.example.goodsprice.common.util.JsonUtils;
import com.example.goodsprice.llm.application.port.out.LlmProviderPort;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCreateDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.application.port.in.ReceiptApprovalInPort;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ReceiptService extends AbstractGenericService<ReceiptDomain, UUID>
    implements ReceiptInPort {

  private final ReceiptRepositoryPort receiptRepository;
  private final ReceiptEventOutPort eventOutPort;
  private final LlmProviderPort llmProvider;
  private final ObjectMapper objectMapper;
  private final ReceiptApprovalInPort receiptApprovalInPort;

  public ReceiptService(
      ReceiptRepositoryPort receiptRepository,
      ReceiptEventOutPort eventOutPort,
      LlmProviderPort llmProvider,
      ObjectMapper objectMapper,
      ReceiptApprovalInPort receiptApprovalInPort) {
    super("Receipt", ErrorCodes.RECEIPT_NOT_FOUND);
    this.receiptRepository = receiptRepository;
    this.eventOutPort = eventOutPort;
    this.llmProvider = llmProvider;
    this.objectMapper = objectMapper;
    this.receiptApprovalInPort = receiptApprovalInPort;
  }

  @Override
  protected GenericRepositoryPort<ReceiptDomain, UUID> getRepository() {
    return receiptRepository;
  }

  @Override
  @Transactional
  @ActivityLog
  public ReceiptDomain upload(byte[] imageBytes, String originalFilename) {
    var imageHash = HashUtils.sha256(imageBytes);
    log.info("Uploading receipt: {} (hash: {})", originalFilename, imageHash);

    var existing = receiptRepository.findByImageHash(imageHash);
    if (Objects.nonNull(existing)) {
      if (existing.isRetryable()) {
        log.info("Removing previous failed receipt: {}", existing.getId());
        super.deleteById(existing.getId());
      } else {
        log.info("Receipt already exists ({}): {}", existing.getStatus(), existing.getId());
        return existing;
      }
    }

    var receipt =
        ReceiptDomain.builder()
            .imageHash(imageHash)
            .originalFilename(originalFilename)
            .status(ReceiptStatus.PENDING)
            .build();
    receipt = save(receipt);
    log.info("Receipt created: {}", receipt.getId());

    // Store image data before firing event to avoid byte[] in memory
    if (imageBytes.length > 0) {
      receiptRepository.updateImageData(receipt.getId(), imageBytes);
    }

    eventOutPort.publishReceiptUploaded(receipt);
    return receipt;
  }

  @Override
  public ReceiptStatus getStatus(UUID id) {
    return findById(id).getStatus();
  }

  @Override
  public void approve(UUID id) {
    receiptApprovalInPort.approve(id);
  }

  @Override
  public void reject(UUID id) {
    var receipt = findById(id);
    receipt.markAsRejected();
    save(receipt);
    log.info("Receipt rejected: {}", id);
  }

  @Override
  public void process(UUID id, byte[] imageBytes) {
    var receipt = findById(id);
    receipt.markAsProcessing();
    save(receipt);
    log.info("Receipt processing started: {}", id);

    try {
      byte[] bytesToProcess = imageBytes;
      if (Objects.isNull(bytesToProcess) || bytesToProcess.length == 0) {
        bytesToProcess = receiptRepository.findById(id).getImageData();
      }
      var base64Image = Base64.getEncoder().encodeToString(bytesToProcess);
      var extractedData = llmProvider.extractReceiptData(base64Image);

      if (Objects.isNull(extractedData) || extractedData.isEmpty()) {
        throw new RuntimeException("LLM returned empty data");
      }

      var extractedDataJson = objectMapper.writeValueAsString(extractedData);
      var storeName = (String) extractedData.get("storeName");
      var storeLocation = (String) extractedData.get("storeLocation");
      var date = (String) extractedData.get("date");
      var totalAmount = extractTotalAmount(extractedData.get("totalAmount"));

      receipt.markAsCompleted(storeName, storeLocation, date, totalAmount, extractedDataJson);
      save(receipt);

      eventOutPort.publishReceiptProcessed(receipt);
      log.info("Receipt processing completed: {}", id);

    } catch (Exception e) {
      log.error("Receipt processing failed: {}", id, e);
      receipt.markAsFailed(e.getMessage());
      save(receipt);
    }
  }

  @Override
  public void insertItems(UUID id, List<Map<String, Object>> items) {
    var receipt = findById(id);
    if (Objects.isNull(items) || items.isEmpty()) {
      throw new IllegalArgumentException(ITEMS_NOT_EMPTY_MSG);
    }
    receipt.markAsCompleted(null, null, null, null, null);
    save(receipt);
    log.info("Receipt items inserted: {}", id);
  }

  @Override
  @Transactional
  @ActivityLog
  public ReceiptDomain create(ReceiptCreateDomain request) {
    var extractedDataJson = JsonUtils.toJson(request);
    var imageHash = JsonUtils.hash256(request);
    var receipt =
        ReceiptDomain.builder()
            .status(ReceiptStatus.COMPLETED)
            .storeName(request.getStoreName())
            .extractedDataJson(extractedDataJson)
            .imageHash(imageHash)
            .totalAmount(request.getTotalAmount())
            .storeLocation(request.getStoreLocation())
            .receiptDate(request.getReceiptDate())
            .build();
    receipt = save(receipt);
    receiptApprovalInPort.approve(receipt.getId());
    return this.findById(receipt.getId());
  }

  private BigDecimal extractTotalAmount(Object value) {
    if (Objects.isNull(value)) return null;
    if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
    try {
      return new BigDecimal(value.toString());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
