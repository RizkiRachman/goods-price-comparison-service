package com.example.goodsprice.activity.application.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ActivityLogDomain {

  private UUID id;
  private String type;
  private String action;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
