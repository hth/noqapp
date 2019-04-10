package com.noqapp.service.exceptions;

/**
 * User: hitender
 * Date: 2019-04-09 14:27
 */
public class PurchaseOrderRefundExternalException extends RuntimeException {
    public PurchaseOrderRefundExternalException(String message) {
        super(message);
    }

    public PurchaseOrderRefundExternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
