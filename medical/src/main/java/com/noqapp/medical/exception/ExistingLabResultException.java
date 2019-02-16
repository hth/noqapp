package com.noqapp.medical.exception;

/**
 * hitender
 * 2019-02-17 00:20
 */
public class ExistingLabResultException extends RuntimeException {

    public ExistingLabResultException(String message) {
        super(message);
    }

    public ExistingLabResultException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
