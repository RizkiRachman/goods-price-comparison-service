package com.example.goodsprice.module.product.repository;

import com.example.goodsprice.module.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}
