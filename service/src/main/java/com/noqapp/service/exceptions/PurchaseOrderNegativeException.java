package com.noqapp.service.exceptions;

/**
 * hitender
 * 9/2/20 2:05 PM
 */
public class PurchaseOrderNegativeException extends RuntimeException {
    public PurchaseOrderNegativeException(String message) {
        super(message);
    }

    public PurchaseOrderNegativeException(String message, Throwable cause) {
        super(message, cause);
    }
}
