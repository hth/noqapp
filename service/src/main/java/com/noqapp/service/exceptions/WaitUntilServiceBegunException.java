package com.noqapp.service.exceptions;

/**
 * hitender
 * 11/26/20 1:44 PM
 */
public class WaitUntilServiceBegunException extends RuntimeException {

    public WaitUntilServiceBegunException(String message) {
        super(message);
    }

    public WaitUntilServiceBegunException(String message, Throwable cause) {
        super(message, cause);
    }
}
