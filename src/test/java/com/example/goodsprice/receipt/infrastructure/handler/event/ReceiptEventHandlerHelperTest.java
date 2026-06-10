package com.example.goodsprice.receipt.infrastructure.handler.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.goodsprice.price.application.domain.model.PriceCreateItem;
import com.example.goodsprice.price.application.port.in.PriceInPort;
import com.example.goodsprice.product.application.domain.model.ProductDomain;
import com.example.goodsprice.product.application.port.in.ProductInPort;
import com.example.goodsprice.receipt.application.domain.model.ReceiptItemDomain;
import com.example.goodsprice.receipt.application.port.out.ReceiptItemRepositoryPort;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import com.example.goodsprice.store.application.port.out.StoreRepositoryPort;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiptEventHandlerHelperTest {

  @Mock private ReceiptItemRepositoryPort receiptItemRepository;
  @Mock private ProductInPort productInPort;
  @Mock private PriceInPort priceInPort;
  @Mock private StoreRepositoryPort storeRepository;

  @InjectMocks private ReceiptEventHandlerHelper helper;

  @Captor private ArgumentCaptor<List<ReceiptItemDomain>> receiptItemsCaptor;

  private final UUID receiptId = UUID.randomUUID();
  private final LocalDate today = LocalDate.now();

  // --- resolveStore ---

  @Test
  void shouldReturnNullWhenStoreNameIsNull() {
    var result = helper.resolveStore(null);
    assertNull(result);
  }

  @Test
  void shouldReturnExistingStoreWhenFoundByName() {
    var store = StoreDomain.builder().id(1L).name("Toko Segar").build();
    when(storeRepository.findByName("Toko Segar")).thenReturn(List.of(store));

    var result = helper.resolveStore("Toko Segar");

    assertEquals(store, result);
    verify(storeRepository).findByName("Toko Segar");
    verify(storeRepository, never()).save(any());
  }

  @Test
  void shouldCreateAndReturnNewStoreWhenNotFoundByName() {
    when(storeRepository.findByName("New Store")).thenReturn(Collections.emptyList());
    var savedStore = StoreDomain.builder().id(2L).name("New Store").build();
    when(storeRepository.save(any())).thenReturn(savedStore);

    var result = helper.resolveStore("New Store");

    assertEquals(savedStore, result);
    verify(storeRepository).findByName("New Store");
    verify(storeRepository).save(any());
  }

  // --- parseDate ---

  @Test
  void shouldReturnTodayWhenDateStrIsNull() {
    var result = helper.parseDate(null);
    assertEquals(today, result);
  }

  @Test
  void shouldParseYyyyMmDdFormat() {
    var result = helper.parseDate("2026-05-08");
    assertEquals(LocalDate.of(2026, 5, 8), result);
  }

  @Test
  void shouldParseDdMmYyyyFormat() {
    var result = helper.parseDate("08/05/2026");
    assertEquals(LocalDate.of(2026, 5, 8), result);
  }

  @Test
  void shouldParseMmDdYyyyFormat() {
    var result = helper.parseDate("06/15/2026");
    assertEquals(LocalDate.of(2026, 6, 15), result);
  }

  @Test
  void shouldParseDdMmYyyyWithDashFormat() {
    var result = helper.parseDate("08-05-2026");
    assertEquals(LocalDate.of(2026, 5, 8), result);
  }

  @Test
  void shouldReturnTodayWhenInvalidDateFormat() {
    var result = helper.parseDate("invalid-date");
    assertEquals(today, result);
  }

  // --- buildReceiptItems ---

  @Test
  void shouldBuildReceiptItemDomainsSuccessfully() {
    var items =
        List.of(
            Map.<String, Object>of(
                "productName",
                "Apple",
                "category",
                "Fruit",
                "unitType",
                "KG",
                "quantity",
                2.0,
                "unitPrice",
                5.0,
                "totalPrice",
                10.0),
            Map.<String, Object>of(
                "productName",
                "Milk",
                "category",
                "Dairy",
                "unitType",
                "LITER",
                "quantity",
                1.0,
                "unitPrice",
                15.0,
                "totalPrice",
                15.0));

    var result = helper.buildReceiptItems(receiptId, items);

    assertEquals(2, result.size());

    var first = result.get(0);
    assertEquals(receiptId, first.getReceiptId());
    assertEquals("Apple", first.getProductName());
    assertEquals("Fruit", first.getCategory());
    assertEquals("KG", first.getUnit());
    assertEquals(2.0, first.getQuantity());
    assertEquals(5.0, first.getUnitPrice());
    assertEquals(10.0, first.getTotalPrice());

    var second = result.get(1);
    assertEquals(receiptId, second.getReceiptId());
    assertEquals("Milk", second.getProductName());
    assertEquals("Dairy", second.getCategory());
    assertEquals("LITER", second.getUnit());
    assertEquals(1.0, second.getQuantity());
    assertEquals(15.0, second.getUnitPrice());
    assertEquals(15.0, second.getTotalPrice());
  }

  @Test
  void shouldReturnEmptyListWhenItemsIsEmpty() {
    var result = helper.buildReceiptItems(receiptId, Collections.emptyList());
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldHandleNullFieldsInItemMap() {
    Map<String, Object> itemMap = new HashMap<>();
    itemMap.put("productName", null);
    itemMap.put("category", null);
    itemMap.put("unitType", null);
    itemMap.put("quantity", null);
    itemMap.put("unitPrice", null);
    itemMap.put("totalPrice", null);
    var items = List.of(itemMap);

    var result = helper.buildReceiptItems(receiptId, items);

    assertEquals(1, result.size());
    var item = result.get(0);
    assertEquals(receiptId, item.getReceiptId());
    assertNull(item.getProductName());
    assertNull(item.getCategory());
    assertNull(item.getUnit());
    assertNull(item.getQuantity());
    assertNull(item.getUnitPrice());
    assertNull(item.getTotalPrice());
  }

  // --- saveReceiptItems ---

  @Test
  void shouldSaveReceiptItemDomains() {
    var items =
        List.of(ReceiptItemDomain.builder().receiptId(receiptId).productName("Apple").build());

    helper.saveReceiptItems(items);

    verify(receiptItemRepository).saveAll(items);
  }

  @Test
  void shouldSaveEmptyReceiptItemDomains() {
    var items = Collections.<ReceiptItemDomain>emptyList();

    helper.saveReceiptItems(items);

    verify(receiptItemRepository).saveAll(items);
  }

  // --- processProductsAndPrices ---

  @Test
  void shouldProcessProductsAndPricesSuccessfully() {
    var store = StoreDomain.builder().id(1L).name("Toko Segar").build();
    var date = LocalDate.of(2026, 5, 8);
    var items =
        List.of(
            Map.<String, Object>of(
                "productName", "Apple",
                "category", "Fruit",
                "unitType", "KG",
                "totalPrice", 10.0,
                "unitPrice", 5.0),
            Map.<String, Object>of(
                "productName", "Milk",
                "category", "Dairy",
                "unitType", "LITER",
                "totalPrice", 15.0,
                "unitPrice", 15.0));

    var product1 = ProductDomain.builder().id(100L).name("Apple").build();
    var product2 = ProductDomain.builder().id(200L).name("Milk").build();
    var productItems =
        List.of(
            new ProductInPort.ProductCreateItem("Apple", "Fruit", "KG"),
            new ProductInPort.ProductCreateItem("Milk", "Dairy", "LITER"));
    when(productInPort.createIfNotExistBatch(productItems))
        .thenReturn(Map.of("Apple", product1, "Milk", product2));

    helper.processProductsAndPrices(items, store, date);

    verify(productInPort).createIfNotExistBatch(productItems);
    verify(priceInPort)
        .createBatch(
            List.of(
                new PriceCreateItem(100L, 1L, 10.0, 5.0, date, false),
                new PriceCreateItem(200L, 1L, 15.0, 15.0, date, false)));
  }

  @Test
  void shouldSkipPriceCreationWhenStoreIsNull() {
    var date = LocalDate.of(2026, 5, 8);
    var items =
        List.of(
            Map.<String, Object>of(
                "productName", "Apple",
                "category", "Fruit",
                "unitType", "KG",
                "totalPrice", 10.0,
                "unitPrice", 5.0));

    var product = ProductDomain.builder().id(100L).name("Apple").build();
    var productItems = List.of(new ProductInPort.ProductCreateItem("Apple", "Fruit", "KG"));
    when(productInPort.createIfNotExistBatch(productItems)).thenReturn(Map.of("Apple", product));

    helper.processProductsAndPrices(items, null, date);

    verify(productInPort).createIfNotExistBatch(productItems);
    verify(priceInPort, never()).createBatch(anyList());
  }

  @Test
  void shouldDoNothingWhenItemsAreEmpty() {
    var store = StoreDomain.builder().id(1L).name("Toko Segar").build();

    helper.processProductsAndPrices(Collections.emptyList(), store, today);

    verify(productInPort, never()).createIfNotExist(anyString(), anyString(), anyString());
    verify(priceInPort, never())
        .create(anyLong(), anyLong(), anyDouble(), anyDouble(), any(), anyBoolean());
  }

  @Test
  void shouldHandleNullValuesInProcessProductsAndPrices() {
    var store = StoreDomain.builder().id(1L).name("Toko Segar").build();
    var date = LocalDate.of(2026, 5, 8);
    Map<String, Object> itemMap = new HashMap<>();
    itemMap.put("productName", "Apple");
    itemMap.put("category", null);
    itemMap.put("unitType", null);
    itemMap.put("totalPrice", null);
    itemMap.put("unitPrice", null);
    var items = List.of(itemMap);

    var product = ProductDomain.builder().id(100L).name("Apple").build();
    var productItems = List.of(new ProductInPort.ProductCreateItem("Apple", null, null));
    when(productInPort.createIfNotExistBatch(productItems)).thenReturn(Map.of("Apple", product));

    helper.processProductsAndPrices(items, store, date);

    verify(productInPort).createIfNotExistBatch(productItems);
    verify(priceInPort)
        .createBatch(List.of(new PriceCreateItem(100L, 1L, null, null, date, false)));
  }
}
