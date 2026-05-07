package com.example.goodsprice.store.application.exception;

import static com.example.goodsprice.common.constant.ErrorMessageConstants.STORE_NOT_FOUND_MSG;

public class StoreNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public StoreNotFoundException(Long id) {
    super(STORE_NOT_FOUND_MSG.formatted(id));
  }
}
