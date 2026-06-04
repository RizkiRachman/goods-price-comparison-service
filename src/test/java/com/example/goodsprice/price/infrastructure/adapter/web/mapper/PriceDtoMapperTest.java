package com.example.goodsprice.price.infrastructure.adapter.web.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.api.model.CheapestPrice;
import com.example.goodsprice.api.model.PriceRecord;
import com.example.goodsprice.api.model.PriceResult;
import com.example.goodsprice.api.model.PriceResultV2;
import com.example.goodsprice.price.application.domain.model.PriceDomain;
import com.example.goodsprice.store.application.domain.model.StoreDomain;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PriceDtoMapperTest {

  private PriceDtoMapper mapper;

  private PriceDomain price;
  private StoreDomain store;

  @BeforeEach
  void setUp() {
    mapper = new PriceDtoMapper();
    price =
        PriceDomain.builder()
            .id(1L)
            .productId(100L)
            .storeId(10L)
            .price(15000.0)
            .unitPrice(14000.0)
            .dateRecorded(LocalDate.of(2026, 6, 1))
            .isPromo(false)
            .build();
    store = StoreDomain.builder().id(10L).name("Toko Segar").location("Jakarta").build();
  }

  @Test
  @DisplayName("Should map to PriceRecord")
  void shouldMapToPriceRecord() {
    PriceRecord record = mapper.toPriceRecord(price, store);

    assertNotNull(record);
    assertEquals(1L, record.getId());
    assertEquals(100L, record.getProductId());
    assertEquals(10L, record.getStoreId());
    assertEquals("Toko Segar", record.getStoreName());
    assertEquals(15000.0, record.getPrice());
    assertEquals(14000.0, record.getUnitPrice());
    assertEquals(
        LocalDate.of(2026, 6, 1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
        record.getDateRecorded());
    assertEquals(false, record.getIsPromo());
    assertEquals(PriceRecord.AvailabilityEnum.IN_STOCK, record.getAvailability());
  }

  @Test
  @DisplayName("Should map to PriceResult")
  void shouldMapToPriceResult() {
    PriceResult result = mapper.toResult(price, store);

    assertNotNull(result);
    assertEquals(10L, result.getStoreId());
    assertEquals("Toko Segar", result.getStoreName());
    assertEquals("Jakarta", result.getStoreLocation());
    assertEquals(15000.0, result.getPrice());
    assertEquals(14000.0, result.getUnitPrice());
    assertEquals(LocalDate.of(2026, 6, 1), result.getDateRecorded());
    assertEquals(false, result.getIsPromo());
  }

  @Test
  @DisplayName("Should map to PriceResultV2")
  void shouldMapToPriceResultV2() {
    PriceResultV2 result = mapper.toResultV2(price, store);

    assertNotNull(result);
    assertEquals(10L, result.getStoreId());
    assertEquals("Toko Segar", result.getStoreName());
    assertEquals(15000.0, result.getPrice());
    assertEquals(14000.0, result.getUnitPrice());
    assertEquals(
        LocalDate.of(2026, 6, 1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
        result.getDateRecorded());
    assertEquals(false, result.getIsPromo());
  }

  @Test
  @DisplayName("Should map to CheapestPrice")
  void shouldMapToCheapestPrice() {
    CheapestPrice cheapest = mapper.toCheapestPrice(price, store);

    assertNotNull(cheapest);
    assertEquals("Toko Segar", cheapest.getStoreName());
    assertEquals(15000.0, cheapest.getPrice());
  }

  @Test
  @DisplayName("Should handle null store in toResult")
  void shouldHandleNullStoreInResult() {
    PriceResult result = mapper.toResult(price, null);

    assertNotNull(result);
    assertNull(result.getStoreName());
    assertNull(result.getStoreLocation());
  }

  @Test
  @DisplayName("Should handle null store in toResultV2")
  void shouldHandleNullStoreInResultV2() {
    PriceResultV2 result = mapper.toResultV2(price, null);

    assertNotNull(result);
    assertNull(result.getStoreName());
  }

  @Test
  @DisplayName("Should handle null store in toCheapestPrice")
  void shouldHandleNullStoreInCheapestPrice() {
    CheapestPrice cheapest = mapper.toCheapestPrice(price, null);

    assertNotNull(cheapest);
    assertNull(cheapest.getStoreName());
  }

  @Test
  @DisplayName("Should handle null dateRecorded in toPriceRecord")
  void shouldHandleNullDateRecordedInPriceRecord() {
    price.setDateRecorded(null);

    PriceRecord record = mapper.toPriceRecord(price, store);

    assertNotNull(record);
    assertNull(record.getDateRecorded());
  }

  @Test
  @DisplayName("Should handle null dateRecorded in toResultV2")
  void shouldHandleNullDateRecordedInResultV2() {
    price.setDateRecorded(null);

    PriceResultV2 result = mapper.toResultV2(price, store);

    assertNotNull(result);
    assertNull(result.getDateRecorded());
  }

  @Test
  @DisplayName("Should handle null price in toCheapestPrice")
  void shouldHandleNullPriceInCheapestPrice() {
    price.setPrice(null);

    CheapestPrice cheapest = mapper.toCheapestPrice(price, store);

    assertNotNull(cheapest);
    assertNull(cheapest.getPrice());
  }
}
