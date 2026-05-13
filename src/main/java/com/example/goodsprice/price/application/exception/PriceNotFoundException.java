package com.example.goodsprice.price.application.exception;

import com.example.goodsprice.common.constant.ErrorCodes;
import com.example.goodsprice.common.constant.ErrorMessageConstants;
import com.example.goodsprice.common.exception.NotFoundException;

public class PriceNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 1L;

  public PriceNotFoundException(Long id) {
    super(ErrorCodes.PRICE_NOT_FOUND, ErrorMessageConstants.PRICE_NOT_FOUND_MSG.formatted(id));
  }
}
