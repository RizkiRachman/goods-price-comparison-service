package com.example.goodsprice.receipt.application.domain.service;

import static com.example.goodsprice.common.constant.ErrorMessageConstants.ITEMS_NOT_EMPTY_MSG;

import com.example.goodsprice.common.util.HashUtils;
import com.example.goodsprice.common.util.JsonUtils;
import com.example.goodsprice.receipt.application.domain.model.ReceiptCreateDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import com.example.goodsprice.receipt.application.exception.ReceiptNotFoundException;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.application.port.out.LlmProviderPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptEventOutPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService implements ReceiptInPort {

  private final ReceiptRepositoryPort receiptRepository;
  private final ReceiptEventOutPort eventOutPort;
  private final LlmProviderPort llmProvider;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  public ReceiptDomain upload(byte[] imageBytes, String originalFilename) {
    var imageHash = HashUtils.sha256(imageBytes);
    log.info("Uploading receipt: {} (hash: {})", originalFilename, imageHash);

    var existing = receiptRepository.findByImageHash(imageHash);
    if (Objects.nonNull(existing)) {
      if (existing.isRetryable()) {
        log.info("Removing previous failed receipt: {}", existing.getId());
        receiptRepository.deleteById(existing.getId());
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
    receipt = receiptRepository.save(receipt);
    log.info("Receipt created: {}", receipt.getId());

    eventOutPort.publishReceiptUploaded(receipt, imageBytes);
    return receipt;
  }

  @Override
  public ReceiptDomain findById(UUID id) {
    var receipt = receiptRepository.findById(id);
    if (Objects.isNull(receipt)) throw new ReceiptNotFoundException(id);
    return receipt;
  }

  @Override
  public ReceiptStatus getStatus(UUID id) {
    var receipt = receiptRepository.findById(id);
    if (Objects.isNull(receipt)) return null;
    return receipt.getStatus();
  }

  @Override
  @Transactional
  public void approve(UUID id) {
    var receipt = findById(id);
    receipt.markAsApproved();
    receiptRepository.save(receipt);
    eventOutPort.publishReceiptApproved(receipt);
    log.info("Receipt approved: {}", id);
  }

  @Override
  public void reject(UUID id) {
    var receipt = findById(id);
    receipt.markAsRejected();
    receiptRepository.save(receipt);
    log.info("Receipt rejected: {}", id);
  }

  @Override
  public void process(UUID id, byte[] imageBytes) {
    var receipt = findById(id);
    receipt.markAsProcessing();
    receiptRepository.save(receipt);
    log.info("Receipt processing started: {}", id);

    try {
      var base64Image = Base64.getEncoder().encodeToString(imageBytes);
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
      receiptRepository.save(receipt);

      eventOutPort.publishReceiptProcessed(receipt);
      log.info("Receipt processing completed: {}", id);

    } catch (Exception e) {
      log.error("Receipt processing failed: {}", id, e);
      receipt.markAsFailed(e.getMessage());
      receiptRepository.save(receipt);
    }
  }

  @Override
  public void insertItems(UUID id, List<Map<String, Object>> items) {
    var receipt = findById(id);
    if (Objects.isNull(items) || items.isEmpty()) {
      throw new IllegalArgumentException(ITEMS_NOT_EMPTY_MSG);
    }
    receipt.markAsCompleted(null, null, null, null, null);
    receiptRepository.save(receipt);
    log.info("Receipt items inserted: {}", id);
  }

  @Override
  @Transactional
  public void create(ReceiptCreateDomain request) {
    var itemsJson = JsonUtils.toJson(request.getItems());
    var imageHash = HashUtils.sha256(itemsJson.getBytes());
    var receipt =
        ReceiptDomain.builder()
            .imageHash(imageHash)
            .status(ReceiptStatus.COMPLETED)
            .storeName(request.getStoreName())
            .extractedDataJson(itemsJson)
            .totalAmount(request.getTotalAmount())
            .storeLocation(request.getStoreLocation())
            .receiptDate(request.getReceiptDate())
            .build();
    receipt = receiptRepository.save(receipt);
    this.approve(receipt.getId());
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
