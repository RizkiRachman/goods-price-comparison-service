package com.example.goodsprice.feedbackquestion.application.port.in.dto;

import com.example.goodsprice.common.dto.PageRequestDto;

public record FeedbackQuestionCriteria(PageRequestDto pageRequest, String search, String status) {}
