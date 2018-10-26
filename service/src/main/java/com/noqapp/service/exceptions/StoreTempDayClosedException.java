package com.noqapp.service.exceptions;

/**
 * hitender
 * 10/26/18 11:19 PM
 */
public class StoreTempDayClosedException extends RuntimeException {

    public StoreTempDayClosedException(String message) {
        super(message);
    }

    public StoreTempDayClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
