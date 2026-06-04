package com.example.goodsprice.unit.infrastructure.adapter.persistence.entity;

import com.example.goodsprice.unit.application.domain.model.UnitType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitEntity {

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

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
