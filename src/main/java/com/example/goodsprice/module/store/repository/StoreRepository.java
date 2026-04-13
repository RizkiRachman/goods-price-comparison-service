package com.example.goodsprice.module.store.repository;

import com.example.goodsprice.module.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByNameAndLocation(String name, String location);

    List<Store> findByName(String name);

    boolean existsByNameAndLocation(String name, String location);
}
