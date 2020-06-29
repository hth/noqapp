package com.noqapp.service.exceptions;

/**
 * hitender
 * 6/28/20 1:30 AM
 */
public class ExpectedServiceBeyondStoreClosingHour extends RuntimeException {

    public ExpectedServiceBeyondStoreClosingHour(String message) {
        super(message);
    }

    public ExpectedServiceBeyondStoreClosingHour(String message, Throwable cause) {
        super(message, cause);
    }
}
