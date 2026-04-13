package com.example.goodsprice.module.price.entity;

import com.example.goodsprice.module.product.entity.Product;
import com.example.goodsprice.module.store.entity.Store;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prices")
@Data
@NoArgsConstructor
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "date_recorded", nullable = false)
    private LocalDate dateRecorded;

    @Column(name = "is_promo", nullable = false)
    private Boolean isPromo = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Price(Product product, Store store, Double price, Double unitPrice, LocalDate dateRecorded, Boolean isPromo) {
        this.product = product;
        this.store = store;
        this.price = price;
        this.unitPrice = unitPrice;
        this.dateRecorded = dateRecorded;
        this.isPromo = isPromo;
    }
}
