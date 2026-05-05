package com.example.goodsprice.store.application.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreDomain {

  private Long id;
  private String name;
  private String location;
  private String chain;
  private String address;
  private Double latitude;
  private Double longitude;
  private String status;
}
