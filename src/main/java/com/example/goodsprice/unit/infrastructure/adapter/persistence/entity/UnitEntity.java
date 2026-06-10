package com.example.goodsprice.unit.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.common.persistence.BaseTimestampEntity;
import com.example.goodsprice.unit.application.domain.model.UnitType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "units")
public class UnitEntity extends BaseTimestampEntity {

  @Id
  @Column(name = "id", length = 50)
  private String id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "symbol", length = 10)
  private String symbol;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private UnitType type;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "status", length = 50)
  private String status;
}
