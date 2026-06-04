package com.example.goodsprice.activity.application.port.in.dto;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.common.dto.PageRequestDto;
import java.time.OffsetDateTime;

public record ActivityLogCriteria(
    PageRequestDto pageRequest,
    ActivityLogType type,
    ActivityLogAction action,
    OffsetDateTime startDate,
    OffsetDateTime endDate) {}
