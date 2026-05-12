package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.common.util.JsonUtils;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptCorrectedEvent;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptCorrectedEventHandler {

  private final ReceiptInPort receiptInPort;
  private final ReceiptEventHandlerHelper helper;

  @Async("receiptApproveProcessorExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ReceiptCorrectedEvent event) {
    log.info("[Async] Processing receipt correction: {}", event.receiptId());

    try {
      var receipt = receiptInPort.findById(event.receiptId());
      var items = JsonUtils.extractItems(receipt.getExtractedDataJson());
      if (Objects.isNull(items) || items.isEmpty()) {
        log.warn("No items found in corrected receipt: {}", event.receiptId());
        return;
      }

      var store = helper.resolveStore(receipt.getStoreName());
      var date = helper.parseDate(receipt.getReceiptDate());

      var receiptItems = helper.buildReceiptItems(event.receiptId(), items);
      helper.saveReceiptItems(receiptItems);
      log.info(
          "Saved {} corrected receipt_items for receipt: {}",
          receiptItems.size(),
          event.receiptId());

      helper.processProductsAndPrices(items, store, date);

      log.info(
          "Receipt correction processing complete: {} ({} items)", event.receiptId(), items.size());

    } catch (Exception e) {
      log.error("Failed to process receipt correction: {}", event.receiptId(), e);
    }
  }
}
