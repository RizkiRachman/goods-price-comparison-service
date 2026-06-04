package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.common.util.JsonUtils;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractReceiptEventHandler {

  protected final ReceiptInPort receiptInPort;
  protected final ReceiptEventHandlerHelper helper;

  protected abstract String getEventTypeLabel();

  protected void processReceiptEvent(UUID receiptId) {
    log.info("[Async] Processing receipt {}: {}", getEventTypeLabel(), receiptId);

    try {
      var receipt = receiptInPort.findById(receiptId);
      var items = JsonUtils.extractItems(receipt.getExtractedDataJson());
      if (Objects.isNull(items) || items.isEmpty()) {
        log.warn("No items found in {} receipt: {}", getEventTypeLabel(), receiptId);
        return;
      }

      var store = helper.resolveStore(receipt.getStoreName());
      var date = helper.parseDate(receipt.getReceiptDate());

      var receiptItems = helper.buildReceiptItems(receiptId, items);
      helper.saveReceiptItems(receiptItems);
      log.info(
          "Saved {} {} receipt_items for receipt: {}",
          receiptItems.size(),
          getEventTypeLabel(),
          receiptId);

      helper.processProductsAndPrices(items, store, date);

      log.info(
          "Receipt {} processing complete: {} ({} items)",
          getEventTypeLabel(),
          receiptId,
          items.size());

    } catch (Exception e) {
      log.error("Failed to process receipt {}: {}", getEventTypeLabel(), receiptId, e);
    }
  }
}
