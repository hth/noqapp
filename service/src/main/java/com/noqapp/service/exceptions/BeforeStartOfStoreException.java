package com.noqapp.service.exceptions;

/**
 * hitender
 * 5/17/20 2:58 AM
 */
public class BeforeStartOfStoreException extends RuntimeException {

    public BeforeStartOfStoreException(String message) {
        super(message);
    }

    public BeforeStartOfStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
