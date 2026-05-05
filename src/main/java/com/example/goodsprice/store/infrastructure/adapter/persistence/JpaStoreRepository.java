package com.example.goodsprice.store.infrastructure.adapter.persistence;

import com.example.goodsprice.store.infrastructure.adapter.persistence.entity.StoreEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaStoreRepository
    extends JpaRepository<StoreEntity, Long>, JpaSpecificationExecutor<StoreEntity> {

  List<StoreEntity> findByName(String name);
}
