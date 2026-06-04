package com.example.goodsprice.unit.infrastructure.adapter.persistence;

import com.example.goodsprice.unit.infrastructure.adapter.persistence.entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUnitRepository
    extends JpaRepository<UnitEntity, String>, JpaSpecificationExecutor<UnitEntity> {}
