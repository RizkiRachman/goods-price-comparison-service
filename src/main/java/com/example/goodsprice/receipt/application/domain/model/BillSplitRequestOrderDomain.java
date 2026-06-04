package com.example.goodsprice.receipt.application.domain.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BillSplitRequestOrderDomain {

  private String name;
  private List<BillSplitOrderDetailDomain> orders;
}
