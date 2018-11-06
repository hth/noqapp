package com.noqapp.service.exceptions;

/**
 * hitender
 * 11/6/18 1:33 PM
 */
public class FailedTransactionException extends RuntimeException {

    public FailedTransactionException(String message) {
        super(message);
    }

    public FailedTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
