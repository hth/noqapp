package com.noqapp.service.exceptions;

/**
 * hitender
 * 2019-01-20 14:22
 */
public class PurchaseOrderFailException extends  RuntimeException {
    public PurchaseOrderFailException(String message) {
        super(message);
    }

    public PurchaseOrderFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
