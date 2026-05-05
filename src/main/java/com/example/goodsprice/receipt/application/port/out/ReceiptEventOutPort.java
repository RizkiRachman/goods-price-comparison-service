package com.example.goodsprice.receipt.application.port.out;

import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;

public interface ReceiptEventOutPort {

  void publishReceiptUploaded(ReceiptDomain receipt, byte[] imageBytes);

  void publishReceiptProcessed(ReceiptDomain receipt);

  void publishReceiptApproved(ReceiptDomain receipt);

  void publishReceiptCorrected(ReceiptDomain receipt);
}
