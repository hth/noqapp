package com.token.view.flow.exception;

/**
 * User: hitender
 * Date: 11/23/16 4:15 PM
 */
public class BusinessRegistrationException extends RuntimeException {
    public BusinessRegistrationException(String message) {
        super(message);
    }

    public BusinessRegistrationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}