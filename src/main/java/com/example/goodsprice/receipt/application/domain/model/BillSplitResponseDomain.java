package com.example.goodsprice.receipt.application.domain.model;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BillSplitResponseDomain {

  private UUID receiptId;
  private BillSplitType type;
  private Integer numberOfParticipants;
  private Double totalAmount;
  private List<BillSplitParticipantDomain> participants;
  private Double unassignedTotal;
}
