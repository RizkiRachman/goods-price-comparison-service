package com.example.goodsprice.unit.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitDomain {

  private String id;
  private String name;
  private String symbol;
  private UnitType type;
  private String description;
  private String status;
}
