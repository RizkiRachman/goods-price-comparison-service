package com.example.goodsprice.price.application.exception;

import static com.example.goodsprice.common.constant.ErrorMessageConstants.PRICE_NOT_FOUND_MSG;

public class PriceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PriceNotFoundException(Long id) {
    super(PRICE_NOT_FOUND_MSG.formatted(id));
  }
}
