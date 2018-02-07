package com.noqapp.view.flow.merchant.exception;

/**
 * hitender
 * 2/8/18 3:18 AM
 */
public class UnAuthorizedAccessException extends RuntimeException {
    public UnAuthorizedAccessException(String message) {
        super(message);
    }

    public UnAuthorizedAccessException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
