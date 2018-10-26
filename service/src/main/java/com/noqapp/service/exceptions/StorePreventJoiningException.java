package com.noqapp.service.exceptions;

/**
 * hitender
 * 10/26/18 11:20 PM
 */
public class StorePreventJoiningException extends RuntimeException {

    public StorePreventJoiningException(String message) {
        super(message);
    }

    public StorePreventJoiningException(String message, Throwable cause) {
        super(message, cause);
    }
}
