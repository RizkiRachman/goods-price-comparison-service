package com.example.goodsprice.shopping.application.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShoppingSavingsDomain {

  private Double comparedToSingleStore;
  private Double percentage;
}
