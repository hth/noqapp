package com.noqapp.service.exceptions;

/**
 * hitender
 * 11/26/21 6:12 PM
 */
public class AccountEmailValidationException extends RuntimeException {
    public AccountEmailValidationException(String message) {
        super(message);
    }

    public AccountEmailValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
