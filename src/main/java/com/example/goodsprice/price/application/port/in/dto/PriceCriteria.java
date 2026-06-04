package com.example.goodsprice.price.application.port.in.dto;

import com.example.goodsprice.common.dto.PageRequestDto;
import java.time.LocalDate;

public record PriceCriteria(
    Long productId,
    LocalDate startDate,
    LocalDate endDate,
    Long storeId,
    Boolean isPromo,
    PageRequestDto pageRequest) {}
