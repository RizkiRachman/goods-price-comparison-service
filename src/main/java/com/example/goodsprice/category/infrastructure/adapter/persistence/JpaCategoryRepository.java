package com.example.goodsprice.category.infrastructure.adapter.persistence;

import com.example.goodsprice.category.infrastructure.adapter.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCategoryRepository
    extends JpaRepository<CategoryEntity, String>, JpaSpecificationExecutor<CategoryEntity> {}
