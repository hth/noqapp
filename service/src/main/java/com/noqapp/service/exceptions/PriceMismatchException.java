package com.noqapp.service.exceptions;

/**
 * hitender
 * 11/7/18 10:43 AM
 */
public class PriceMismatchException extends RuntimeException {

    public PriceMismatchException(String message) {
        super(message);
    }

    public PriceMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
