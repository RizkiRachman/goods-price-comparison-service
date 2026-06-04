package com.example.goodsprice.store.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreCriteria {
  private String name;
  private String location;
  private String chain;
  private String address;
  private Double latitude;
  private Double longitude;
}
