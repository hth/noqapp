package com.noqapp.service.exceptions;

/**
 * hitender
 * 2019-03-13 10:11
 */
public class PurchaseOrderPartialException extends RuntimeException {
    public PurchaseOrderPartialException(String message) {
        super(message);
    }

    public PurchaseOrderPartialException(String message, Throwable cause) {
        super(message, cause);
    }
}
