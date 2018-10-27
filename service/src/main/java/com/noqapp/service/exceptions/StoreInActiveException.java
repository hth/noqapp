package com.noqapp.service.exceptions;

/**
 * hitender
 * 10/27/18 9:44 AM
 */
public class StoreInActiveException extends RuntimeException {

    public StoreInActiveException(String message) {
        super(message);
    }

    public StoreInActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
