package com.example.goodsprice.receipt.application.exception;

import static com.example.goodsprice.common.constant.ErrorMessageConstants.RECEIPT_NOT_FOUND_MSG;

import java.util.UUID;

public class ReceiptNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReceiptNotFoundException(UUID id) {
    super(RECEIPT_NOT_FOUND_MSG.formatted(id));
  }

  public ReceiptNotFoundException(String message) {
    super(message);
  }
}
