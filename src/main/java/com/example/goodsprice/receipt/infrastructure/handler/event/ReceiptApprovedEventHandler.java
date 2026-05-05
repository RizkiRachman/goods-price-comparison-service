package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.common.util.JsonUtils;
import com.example.goodsprice.common.util.NumberUtils;
import com.example.goodsprice.common.util.ProductNameUtils;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.receipt.application.domain.model.ReceiptItem;
import com.example.goodsprice.receipt.application.port.in.ReceiptInPort;
import com.example.goodsprice.receipt.application.port.out.ReceiptItemRepositoryPort;
import com.example.goodsprice.receipt.infrastructure.adapter.event.ReceiptApprovedEvent;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class ReceiptApprovedEventHandler {

  private final ReceiptInPort receiptInPort;
  private final ReceiptItemRepositoryPort receiptItemRepository;
  private final ProductInPort productInPort;
  private final PriceInPort priceInPort;
  private final StoreRepositoryPort storeRepository;

  @Async("receiptApproveProcessorExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ReceiptApprovedEvent event) {
    log.info("[Async] Processing receipt approval: {}", event.receiptId());

    try {
      var receipt = receiptInPort.findById(event.receiptId());
      var items = JsonUtils.extractItems(receipt.getExtractedDataJson());
      if (Objects.isNull(items) || items.isEmpty()) {
        log.warn("No items found in receipt: {}", event.receiptId());
        return;
      }

      var store = resolveStore(receipt.getStoreName());
      var date = parseDate(receipt.getReceiptDate());

      // Step 1: Store receipt_items
      var receiptItemEntities =
          items.stream()
              .map(
                  item ->
                      ReceiptItem.builder()
                          .receiptId(event.receiptId())
                          .productName((String) item.get("productName"))
                          .category((String) item.get("category"))
                          .unit((String) item.get("unitType"))
                          .quantity(NumberUtils.toDouble(item.get("quantity")))
                          .unitPrice(NumberUtils.toDouble(item.get("unitPrice")))
                          .totalPrice(NumberUtils.toDouble(item.get("totalPrice")))
                          .build())
              .toList();
      receiptItemRepository.saveAll(receiptItemEntities);
      log.info(
          "Saved {} receipt_items for receipt: {}", receiptItemEntities.size(), event.receiptId());

      // Step 2: Create products and prices
      for (var item : items) {
        var productName = (String) item.get("productName");
        var category = (String) item.get("category");
        var unit = (String) item.get("unitType");
        var totalPrice = NumberUtils.toDouble(item.get("totalPrice"));
        var unitPrice = NumberUtils.toDouble(item.get("unitPrice"));

        var cleanedName = ProductNameUtils.cleanProductName(productName, unit);
        var product = productInPort.createIfNotExist(cleanedName, category, unit);
        if (Objects.nonNull(store)) {
          priceInPort.create(product.getId(), store.getId(), totalPrice, unitPrice, date, false);
        }
      }

      log.info(
          "Receipt approval processing complete: {} ({} items)", event.receiptId(), items.size());

    } catch (Exception e) {
      log.error("Failed to process receipt approval: {}", event.receiptId(), e);
    }
  }

  private StoreDomain resolveStore(String storeName) {
    if (Objects.isNull(storeName)) return null;
    var stores = storeRepository.findByName(storeName);
    if (!stores.isEmpty()) return stores.get(0);

    var store = StoreDomain.builder().name(storeName).build();
    return storeRepository.save(store);
  }

  private LocalDate parseDate(String dateStr) {
    if (Objects.isNull(dateStr)) return LocalDate.now();
    var formats = new String[] {"yyyy-MM-dd", "dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy"};
    for (var fmt : formats) {
      try {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(fmt));
      } catch (Exception ignored) {
      }
    }
    return LocalDate.now();
  }
}
