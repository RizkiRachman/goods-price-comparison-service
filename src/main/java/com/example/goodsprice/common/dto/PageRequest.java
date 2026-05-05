package com.example.goodsprice.common.dto;

public record PageRequest(int page, int size, String sortBy, String sortDirection) {

  public PageRequest {
    if (page < 0) page = 0;
    if (size <= 0) size = 20;
  }
}
