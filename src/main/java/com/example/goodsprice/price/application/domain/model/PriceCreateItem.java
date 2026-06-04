package com.example.goodsprice.price.application.domain.model;

import java.time.LocalDate;

/** Value object for batch price creation. */
public record PriceCreateItem(
    Long productId,
    Long storeId,
    Double totalPrice,
    Double unitPrice,
    LocalDate dateRecorded,
    boolean isPromo) {}
