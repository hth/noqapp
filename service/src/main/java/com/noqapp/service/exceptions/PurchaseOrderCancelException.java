package com.noqapp.service.exceptions;

/**
 * User: hitender
 * Date: 2019-04-10 15:37
 */
public class PurchaseOrderCancelException extends RuntimeException {
    public PurchaseOrderCancelException(String message) {
        super(message);
    }

    public PurchaseOrderCancelException(String message, Throwable cause) {
        super(message, cause);
    }
}
