package com.example.goodsprice.category.application.port.in.dto;

import com.example.goodsprice.common.dto.PageRequestDto;

public record CategoryCriteria(PageRequestDto pageRequest, String search, String status) {}
