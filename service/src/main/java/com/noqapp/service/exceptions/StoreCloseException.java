package com.noqapp.service.exceptions;

/**
 * hitender
 * 10/26/18 9:48 PM
 */
public class StoreCloseException extends RuntimeException {

    public StoreCloseException(String message) {
        super(message);
    }

    public StoreCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
