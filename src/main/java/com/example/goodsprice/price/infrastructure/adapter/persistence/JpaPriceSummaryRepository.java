package com.example.goodsprice.price.infrastructure.adapter.persistence;

import com.example.goodsprice.price.infrastructure.adapter.persistence.entity.PriceSummaryEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPriceSummaryRepository extends JpaRepository<PriceSummaryEntity, Long> {

  List<PriceSummaryEntity> findByProductIdIn(Set<Long> productIds);
}
