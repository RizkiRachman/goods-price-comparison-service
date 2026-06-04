package com.example.goodsprice.receipt.infrastructure.adapter.web.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.goodsprice.api.model.BillSplitOrder;
import com.example.goodsprice.api.model.BillSplitOrderDetail;
import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import com.example.goodsprice.receipt.application.domain.model.BillSplitItemDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitParticipantDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitResponseDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BillSplitDtoMapperTest {

  private final BillSplitDtoMapper mapper = new BillSplitDtoMapper();

  @Test
  void shouldMapToRequestDomain() {
    var request =
        new BillSplitRequest().type(BillSplitRequest.TypeEnum.RATIO).numberOfParticipants(4);

    var result = mapper.toRequestDomain(request);

    assertNotNull(result);
    assertEquals(BillSplitType.RATIO, result.getType());
    assertEquals(4, result.getNumberOfParticipants());
  }

  @Test
  void shouldReturnNullWhenToRequestDomainInputIsNull() {
    assertNull(mapper.toRequestDomain(null));
  }

  @Test
  void shouldMapToResponseDto() {
    var receiptId = UUID.randomUUID();
    var domain =
        BillSplitResponseDomain.builder()
            .receiptId(receiptId)
            .type(BillSplitType.RATIO)
            .numberOfParticipants(2)
            .totalAmount(100.0)
            .unassignedTotal(0.0)
            .participants(
                List.of(
                    BillSplitParticipantDomain.builder()
                        .name("Alice")
                        .items(
                            List.of(
                                BillSplitItemDomain.builder()
                                    .productId(1L)
                                    .productName("Apple")
                                    .quantity(2.0)
                                    .unitPrice(5.0)
                                    .subtotal(10.0)
                                    .build()))
                        .subtotal(10.0)
                        .build()))
            .build();

    var result = mapper.toResponseDto(domain);

    assertNotNull(result);
    assertEquals(receiptId, result.getReceiptId());
    assertEquals(BillSplitResponse.TypeEnum.RATIO, result.getType());
    assertEquals(2, result.getNumberOfParticipants());
    assertEquals(100.0, result.getTotalAmount());
    assertEquals(0.0, result.getUnassignedTotal());
    assertNotNull(result.getParticipants());
    assertEquals(1, result.getParticipants().size());
    var participant = result.getParticipants().get(0);
    assertEquals("Alice", participant.getName());
    assertEquals(10.0, participant.getSubtotal());
    assertNotNull(participant.getItems());
    assertEquals(1, participant.getItems().size());
    var item = participant.getItems().get(0);
    assertEquals(1L, item.getProductId());
    assertEquals("Apple", item.getProductName());
    assertEquals(2.0, item.getQuantity());
    assertEquals(5.0, item.getUnitPrice());
    assertEquals(10.0, item.getSubtotal());
  }

  @Test
  void shouldReturnNullWhenToResponseDtoInputIsNull() {
    assertNull(mapper.toResponseDto(null));
  }

  @Test
  void shouldMapToDomainType() {
    assertEquals(
        BillSplitType.RATIO,
        mapper
            .toRequestDomain(new BillSplitRequest().type(BillSplitRequest.TypeEnum.RATIO))
            .getType());
    assertEquals(
        BillSplitType.SELECTION,
        mapper
            .toRequestDomain(new BillSplitRequest().type(BillSplitRequest.TypeEnum.SELECTION))
            .getType());
  }

  @Test
  void shouldReturnNullWhenToDomainTypeIsNull() {
    var result = mapper.toRequestDomain(new BillSplitRequest().type(null));
    assertNull(result.getType());
  }

  @Test
  void shouldMapToSpecType() {
    var result =
        mapper.toResponseDto(
            BillSplitResponseDomain.builder().type(BillSplitType.SELECTION).build());
    assertEquals(BillSplitResponse.TypeEnum.SELECTION, result.getType());
  }

  @Test
  void shouldMapToOrderDomainWithOrders() {
    var request =
        new BillSplitRequest()
            .type(BillSplitRequest.TypeEnum.SELECTION)
            .numberOfParticipants(1)
            .orders(
                List.of(
                    new BillSplitOrder()
                        .name("Alice")
                        .details(List.of(new BillSplitOrderDetail().name("Apple").quantity(2.0)))));

    var result = mapper.toRequestDomain(request);

    assertNotNull(result.getOrders());
    assertEquals(1, result.getOrders().size());
    var order = result.getOrders().get(0);
    assertEquals("Alice", order.getName());
    assertNotNull(order.getOrders());
    assertEquals(1, order.getOrders().size());
    var detail = order.getOrders().get(0);
    assertEquals("Apple", detail.getName());
    assertEquals(2.0, detail.getQuantity());
  }

  @Test
  void shouldMapToOrderDomainWithEmptyOrders() {
    var request =
        new BillSplitRequest()
            .type(BillSplitRequest.TypeEnum.SELECTION)
            .numberOfParticipants(1)
            .orders(List.of());

    var result = mapper.toRequestDomain(request);

    assertNotNull(result.getOrders());
    assertEquals(0, result.getOrders().size());
  }

  @Test
  void shouldMapToOrderDomainWithNullOrders() {
    var request =
        new BillSplitRequest().type(BillSplitRequest.TypeEnum.SELECTION).numberOfParticipants(1);

    var result = mapper.toRequestDomain(request);

    assertNotNull(result.getOrders());
    assertEquals(0, result.getOrders().size());
  }

  @Test
  void shouldMapWithNullParticipants() {
    var result = mapper.toResponseDto(BillSplitResponseDomain.builder().participants(null).build());

    assertNotNull(result.getParticipants());
    assertEquals(0, result.getParticipants().size());
  }

  @Test
  void shouldMapWithNullItemsInParticipant() {
    var result =
        mapper.toResponseDto(
            BillSplitResponseDomain.builder()
                .participants(
                    List.of(BillSplitParticipantDomain.builder().name("Alice").items(null).build()))
                .build());

    assertNotNull(result.getParticipants());
    assertEquals(1, result.getParticipants().size());
    assertNotNull(result.getParticipants().get(0).getItems());
    assertEquals(0, result.getParticipants().get(0).getItems().size());
  }
}
