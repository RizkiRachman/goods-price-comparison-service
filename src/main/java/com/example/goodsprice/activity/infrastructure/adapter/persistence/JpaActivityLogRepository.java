package com.example.goodsprice.activity.infrastructure.adapter.persistence;

import com.example.goodsprice.activity.infrastructure.adapter.persistence.entity.ActivityLogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JpaActivityLogRepository
    extends JpaRepository<ActivityLogEntity, UUID>, JpaSpecificationExecutor<ActivityLogEntity> {}
