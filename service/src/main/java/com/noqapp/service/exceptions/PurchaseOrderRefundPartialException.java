package com.noqapp.service.exceptions;

/**
 * hitender
 * 2019-03-13 10:11
 */
public class PurchaseOrderRefundPartialException extends RuntimeException {
    public PurchaseOrderRefundPartialException(String message) {
        super(message);
    }

    public PurchaseOrderRefundPartialException(String message, Throwable cause) {
        super(message, cause);
    }
}
