package com.example.goodsprice.receipt.application.port.in;

import com.example.goodsprice.receipt.application.domain.model.ReceiptCorrectionDomain;
import com.example.goodsprice.receipt.application.domain.model.ReceiptDomain;
import java.util.UUID;

public interface ReceiptCorrectionInPort {

  ReceiptDomain correct(UUID receiptId, ReceiptCorrectionDomain correction);
}
