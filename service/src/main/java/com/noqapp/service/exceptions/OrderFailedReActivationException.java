package com.noqapp.service.exceptions;

/**
 * hitender
 * 11/8/18 12:48 AM
 */
public class OrderFailedReActivationException extends RuntimeException {

    public OrderFailedReActivationException(String message) {
        super(message);
    }

    public OrderFailedReActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
