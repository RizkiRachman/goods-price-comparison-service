package com.example.goodsprice.receipt.infrastructure.adapter.web.mapper;

import com.example.goodsprice.api.model.BillSplitItem;
import com.example.goodsprice.api.model.BillSplitOrder;
import com.example.goodsprice.api.model.BillSplitOrderDetail;
import com.example.goodsprice.api.model.BillSplitParticipant;
import com.example.goodsprice.api.model.BillSplitRequest;
import com.example.goodsprice.api.model.BillSplitResponse;
import com.example.goodsprice.common.web.mapper.DtoMapperSupport;
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
public class BillSplitDtoMapper implements DtoMapperSupport {

  public BillSplitRequestDomain toRequestDomain(BillSplitRequest request) {
    return mapIfNotNull(
        request,
        req ->
            BillSplitRequestDomain.builder()
                .type(toDomainType(req.getType()))
                .numberOfParticipants(req.getNumberOfParticipants())
                .orders(toOrderDomain(req.getOrders()))
                .build());
  }

  public BillSplitResponse toResponseDto(BillSplitResponseDomain domain) {
    return mapIfNotNull(
        domain,
        d ->
            new BillSplitResponse()
                .receiptId(d.getReceiptId())
                .type(toSpecType(d.getType()))
                .numberOfParticipants(d.getNumberOfParticipants())
                .totalAmount(d.getTotalAmount())
                .participants(toParticipantDtos(d.getParticipants()))
                .unassignedTotal(d.getUnassignedTotal()));
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
    return mapIfNotNull(
        participant,
        p ->
            new BillSplitParticipant()
                .name(p.getName())
                .items(toItemDtos(p.getItems()))
                .subtotal(p.getSubtotal()));
  }

  private List<BillSplitItem> toItemDtos(List<BillSplitItemDomain> items) {
    if (Objects.isNull(items)) return Collections.emptyList();
    return items.stream().map(this::toItemDto).toList();
  }

  private BillSplitItem toItemDto(BillSplitItemDomain item) {
    return mapIfNotNull(
        item,
        i ->
            new BillSplitItem()
                .productId(i.getProductId())
                .productName(i.getProductName())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .subtotal(i.getSubtotal()));
  }
}
