package com.example.goodsprice.receipt.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.BillSplitItem;
import com.example.goodsprice.api.model.BillSplitOrder;
import com.example.goodsprice.api.model.BillSplitOrderDetail;
import com.example.goodsprice.api.model.BillSplitParticipant;
import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import com.example.goodsprice.receipt.application.domain.model.BillSplitItemDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitOrderDetailDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitParticipantDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitRequestDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitRequestOrderDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitResponseDomain;
import com.example.goodsprice.receipt.application.domain.model.BillSplitType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class BillSplitDtoMapper {

  public BillSplitRequestDomain toRequestDomain(BillSplitRequest request) {
    if (Objects.isNull(request)) return null;
    return BillSplitRequestDomain.builder()
        .type(toDomainType(request.getType()))
        .numberOfParticipants(request.getNumberOfParticipants())
        .orders(toOrderDomain(request.getOrders()))
        .build();
  }

  public BillSplitResponse toResponseDto(BillSplitResponseDomain domain) {
    if (Objects.isNull(domain)) return null;
    return new BillSplitResponse()
        .receiptId(domain.getReceiptId())
        .type(toSpecType(domain.getType()))
        .numberOfParticipants(domain.getNumberOfParticipants())
        .totalAmount(domain.getTotalAmount())
        .participants(toParticipantDtos(domain.getParticipants()))
        .unassignedTotal(domain.getUnassignedTotal());
  }

  private BillSplitType toDomainType(BillSplitRequest.TypeEnum type) {
    if (Objects.isNull(type)) return null;
    return BillSplitType.valueOf(type.name());
  }

  private BillSplitResponse.TypeEnum toSpecType(BillSplitType type) {
    if (Objects.isNull(type)) return null;
    return BillSplitResponse.TypeEnum.fromValue(type.name());
  }

  private List<BillSplitRequestOrderDomain> toOrderDomain(List<BillSplitOrder> orders) {
    if (Objects.isNull(orders)) return Collections.emptyList();
    return orders.stream().filter(Objects::nonNull).map(this::toOrderDomain).toList();
  }

  private BillSplitRequestOrderDomain toOrderDomain(BillSplitOrder order) {
    return BillSplitRequestOrderDomain.builder()
        .name(order.getName())
        .orders(
            order.getDetails().stream()
                .filter(Objects::nonNull)
                .map(this::toOrderDetailDomain)
                .toList())
        .build();
  }

  private BillSplitOrderDetailDomain toOrderDetailDomain(BillSplitOrderDetail order) {
    return BillSplitOrderDetailDomain.builder()
        .quantity(order.getQuantity())
        .productId(0L)
        .name(order.getName())
        .build();
  }

  private List<BillSplitParticipant> toParticipantDtos(
      List<BillSplitParticipantDomain> participants) {
    if (Objects.isNull(participants)) return Collections.emptyList();
    return participants.stream().map(this::toParticipantDto).toList();
  }

  private BillSplitParticipant toParticipantDto(BillSplitParticipantDomain participant) {
    if (Objects.isNull(participant)) return null;
    return new BillSplitParticipant()
        .name(participant.getName())
        .items(toItemDtos(participant.getItems()))
        .subtotal(participant.getSubtotal());
  }

  private List<BillSplitItem> toItemDtos(List<BillSplitItemDomain> items) {
    if (Objects.isNull(items)) return Collections.emptyList();
    return items.stream().map(this::toItemDto).toList();
  }

  private BillSplitItem toItemDto(BillSplitItemDomain item) {
    if (Objects.isNull(item)) return null;
    return new BillSplitItem()
        .productId(item.getProductId())
        .productName(item.getProductName())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .subtotal(item.getSubtotal());
  }
}
