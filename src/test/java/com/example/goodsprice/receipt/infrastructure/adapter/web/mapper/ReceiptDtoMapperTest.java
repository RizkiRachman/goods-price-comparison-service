package com.example.goodsprice.receipt.infrastructure.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.goodsprice.api.model.ReceiptCreateRequest;
import com.example.goodsprice.api.model.ReceiptItem;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class ReceiptDtoMapperTest {

  private final ReceiptDtoMapper mapper = new ReceiptDtoMapper();

  @Test
  void shouldMapToCreateDomain() {
    var items =
        List.of(
            new ReceiptItem()
                .productName("Apple")
                .category("Fruit")
                .quantity(2.0)
                .unitPrice(5.0)
                .totalPrice(10.0)
                .unit("KG"));
    var request =
        new ReceiptCreateRequest()
            .storeName("Toko Segar")
            .storeLocation("Jakarta")
            .date(LocalDate.of(2026, 5, 8))
            .totalAmount(10.0)
            .items(items);

    var domain = mapper.toCreateDomain(request);

    assertThat(domain).isNotNull();
    assertThat(domain.getStoreName()).isEqualTo("Toko Segar");
    assertThat(domain.getStoreLocation()).isEqualTo("Jakarta");
    assertThat(domain.getReceiptDate()).isEqualTo("2026-05-08");
    assertThat(domain.getTotalAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(domain.getItems()).hasSize(1);
    var item = domain.getItems().get(0);
    assertThat(item.getProductName()).isEqualTo("Apple");
    assertThat(item.getCategory()).isEqualTo("Fruit");
    assertThat(item.getQuantity()).isEqualTo(2.0);
    assertThat(item.getUnitPrice()).isEqualTo(5.0);
    assertThat(item.getTotalPrice()).isEqualTo(10.0);
    assertThat(item.getUnit()).isEqualTo("KG");
  }

  @Test
  void shouldMapToCreateDomainWithNullItems() {
    var request =
        new ReceiptCreateRequest()
            .storeName("Toko Segar")
            .storeLocation("Jakarta")
            .date(LocalDate.of(2026, 5, 8))
            .totalAmount(10.0);

    var domain = mapper.toCreateDomain(request);

    assertThat(domain).isNotNull();
    assertThat(domain.getItems()).isEmpty();
  }

  @Test
  void shouldMapToCreateDomainWithNullTotalAmount() {
    var request =
        new ReceiptCreateRequest()
            .storeName("Toko Segar")
            .date(LocalDate.of(2026, 5, 8))
            .items(
                List.of(
                    new ReceiptItem()
                        .productName("Milk")
                        .quantity(1.0)
                        .unitPrice(15.0)
                        .totalPrice(15.0)));

    var domain = mapper.toCreateDomain(request);

    assertThat(domain).isNotNull();
    assertThat(domain.getTotalAmount()).isNull();
    assertThat(domain.getItems()).hasSize(1);
  }

  @Test
  void shouldMapToCreateDomainWithNullRequest() {
    var domain = mapper.toCreateDomain(null);
    assertThat(domain).isNull();
  }

  @Test
  void shouldMapItemToDomain() {
    var item =
        new ReceiptItem()
            .productName("Apple")
            .category("Fruit")
            .quantity(2.0)
            .unitPrice(5.0)
            .totalPrice(10.0)
            .unit("KG");

    var domain = mapper.toItemDomain(item);

    assertThat(domain).isNotNull();
    assertThat(domain.getProductName()).isEqualTo("Apple");
    assertThat(domain.getQuantity()).isEqualTo(2.0);
    assertThat(domain.getUnitPrice()).isEqualTo(5.0);
    assertThat(domain.getTotalPrice()).isEqualTo(10.0);
    assertThat(domain.getUnit()).isEqualTo("KG");
  }

  @Test
  void shouldMapItemToDomainWithNullInput() {
    var domain = mapper.toItemDomain(null);
    assertThat(domain).isNull();
  }

  @Test
  void shouldMapItemFromMap() {
    var itemMap =
        Map.<String, Object>of(
            "productName",
            "Apple",
            "category",
            "Fruit",
            "quantity",
            2.0,
            "unitPrice",
            5.0,
            "totalPrice",
            10.0,
            "unitType",
            "KG");

    var item = mapper.toItem(itemMap);

    assertThat(item).isNotNull();
    assertThat(item.getProductName()).isEqualTo("Apple");
    assertThat(item.getCategory()).isEqualTo("Fruit");
    assertThat(item.getQuantity()).isEqualTo(2.0);
    assertThat(item.getUnitPrice()).isEqualTo(5.0);
    assertThat(item.getTotalPrice()).isEqualTo(10.0);
    assertThat(item.getUnit()).isEqualTo("KG");
  }

  @Test
  void shouldMapItemFromMapWithNullInput() {
    var item = mapper.toItem(null);
    assertThat(item).isNull();
  }

  @Test
  void shouldMapToResultResponse() {
    var domain =
        ReceiptDomain.builder()
            .id(UUID.randomUUID())
            .storeName("Toko Segar")
            .storeLocation("Jakarta")
            .status(ReceiptStatus.COMPLETED)
            .totalAmount(new BigDecimal("10.00"))
            .receiptDate("2026-05-08")
            .extractedDataJson(
                "{\"items\":[{\"productName\":\"Apple\",\"category\":\"Fruit\","
                    + "\"quantity\":2.0,\"unitPrice\":5.0,\"totalPrice\":10.0,\"unitType\":\"KG\"}]}")
            .build();

    var response = mapper.toResultResponse(domain);

    assertThat(response).isNotNull();
    assertThat(response.getReceiptId()).isEqualTo(domain.getId());
    assertThat(response.getStoreName()).isEqualTo("Toko Segar");
    assertThat(response.getStoreLocation()).isEqualTo("Jakarta");
    assertThat(response.getTotalAmount()).isEqualTo(10.0);
    assertThat(response.getItems()).hasSize(1);
    assertThat(response.getItems().get(0).getProductName()).isEqualTo("Apple");
  }

  @Test
  void shouldMapToResultResponseWithNullReceipt() {
    var response = mapper.toResultResponse(null);
    assertThat(response).isNull();
  }

  @Test
  void shouldMapToResultResponseWithNullExtractedData() {
    var domain = ReceiptDomain.builder().id(UUID.randomUUID()).storeName("Toko Segar").build();

    var response = mapper.toResultResponse(domain);

    assertThat(response).isNotNull();
    assertThat(response.getItems()).isEmpty();
  }

  @Test
  void shouldMapStatus() {
    assertThat(mapper.toStatus(ReceiptStatus.PENDING))
        .isEqualTo(com.example.goodsprice.api.model.Status.PENDING);
    assertThat(mapper.toStatus(ReceiptStatus.PROCESSING))
        .isEqualTo(com.example.goodsprice.api.model.Status.PENDING);
    assertThat(mapper.toStatus(ReceiptStatus.PENDING_REVIEW))
        .isEqualTo(com.example.goodsprice.api.model.Status.PENDING);
    assertThat(mapper.toStatus(ReceiptStatus.COMPLETED))
        .isEqualTo(com.example.goodsprice.api.model.Status.COMPLETED);
    assertThat(mapper.toStatus(ReceiptStatus.APPROVED))
        .isEqualTo(com.example.goodsprice.api.model.Status.APPROVED);
    assertThat(mapper.toStatus(ReceiptStatus.REJECTED))
        .isEqualTo(com.example.goodsprice.api.model.Status.REJECTED);
    assertThat(mapper.toStatus(ReceiptStatus.FAILED))
        .isEqualTo(com.example.goodsprice.api.model.Status.FAILED);
  }

  @Test
  void shouldMapStatusWithNullInput() {
    assertThat(mapper.toStatus(null)).isEqualTo(com.example.goodsprice.api.model.Status.PENDING);
  }
}
