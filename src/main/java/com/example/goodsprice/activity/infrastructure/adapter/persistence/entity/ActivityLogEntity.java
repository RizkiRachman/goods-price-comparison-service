package com.example.goodsprice.activity.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.activity.application.domain.model.ActivityLogAction;
import com.example.goodsprice.activity.application.domain.model.ActivityLogType;
import com.example.goodsprice.common.persistence.BaseTimestampEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity_logs")
public class ActivityLogEntity extends BaseTimestampEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  private ActivityLogType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 20)
  private ActivityLogAction action;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;
}
