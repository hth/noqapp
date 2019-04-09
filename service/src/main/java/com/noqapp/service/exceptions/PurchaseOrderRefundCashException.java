package com.noqapp.service.exceptions;

/**
 * User: hitender
 * Date: 2019-04-09 14:27
 */
public class PurchaseOrderRefundCashException extends RuntimeException {
    public PurchaseOrderRefundCashException(String message) {
        super(message);
    }

    public PurchaseOrderRefundCashException(String message, Throwable cause) {
        super(message, cause);
    }
}
