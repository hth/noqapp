package com.noqapp.service.exceptions;

/**
 * hitender
 * 10/26/18 9:48 PM
 */
public class StoreDayClosedException extends RuntimeException {

    public StoreDayClosedException(String message) {
        super(message);
    }

    public StoreDayClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
