package com.noqapp.service.exceptions;

/**
 * hitender
 * 7/3/20 9:19 AM
 */
public class AlreadyServicedTodayException extends RuntimeException {

    public AlreadyServicedTodayException(String message) {
        super(message);
    }

    public AlreadyServicedTodayException(String message, Throwable cause) {
        super(message, cause);
    }
}
