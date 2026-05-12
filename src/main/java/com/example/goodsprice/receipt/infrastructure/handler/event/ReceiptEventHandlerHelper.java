package com.example.goodsprice.receipt.infrastructure.handler.event;

import com.example.goodsprice.common.util.NumberUtils;
import com.example.goodsprice.common.util.ProductNameUtils;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.receipt.application.domain.model.ReceiptItem;
import com.example.goodsprice.receipt.application.port.out.ReceiptItemRepositoryPort;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventHandlerHelper {

  private final ReceiptItemRepositoryPort receiptItemRepository;
  private final ProductInPort productInPort;
  private final PriceInPort priceInPort;
  private final StoreRepositoryPort storeRepository;

  public StoreDomain resolveStore(String storeName) {
    if (Objects.isNull(storeName)) return null;
    var stores = storeRepository.findByName(storeName);
    if (!stores.isEmpty()) return stores.get(0);
    var store = StoreDomain.builder().name(storeName).build();
    return storeRepository.save(store);
  }

  public LocalDate parseDate(String dateStr) {
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

  public List<ReceiptItem> buildReceiptItems(UUID receiptId, List<Map<String, Object>> items) {
    return items.stream()
        .map(
            item ->
                ReceiptItem.builder()
                    .receiptId(receiptId)
                    .productName((String) item.get("productName"))
                    .category((String) item.get("category"))
                    .unit((String) item.get("unitType"))
                    .quantity(NumberUtils.toDouble(item.get("quantity")))
                    .unitPrice(NumberUtils.toDouble(item.get("unitPrice")))
                    .totalPrice(NumberUtils.toDouble(item.get("totalPrice")))
                    .build())
        .toList();
  }

  public void saveReceiptItems(List<ReceiptItem> items) {
    receiptItemRepository.saveAll(items);
  }

  public void processProductsAndPrices(
      List<Map<String, Object>> items, StoreDomain store, LocalDate date) {
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
  }
}
