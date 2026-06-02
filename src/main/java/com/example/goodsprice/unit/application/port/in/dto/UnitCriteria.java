package com.example.goodsprice.unit.application.port.in.dto;

import com.example.goodsprice.common.dto.PageRequestDto;

public record UnitCriteria(PageRequestDto pageRequest, String search, String type, String status) {}
