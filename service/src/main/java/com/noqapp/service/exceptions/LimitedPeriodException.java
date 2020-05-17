package com.noqapp.service.exceptions;

/**
 * hitender
 * 5/17/20 2:15 PM
 */
public class LimitedPeriodException extends RuntimeException {

    public LimitedPeriodException(String message) {
        super(message);
    }

    public LimitedPeriodException(String message, Throwable cause) {
        super(message, cause);
    }
}
