package com.example.goodsprice.store.application.port.in.dto;

import com.example.goodsprice.common.dto.PageRequestDto;

public record StoreCriteria(
    PageRequestDto pageRequest, String search, String status, String chain, String location) {}
