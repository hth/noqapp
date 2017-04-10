package com.noqapp.view.flow.exception;

/**
 * User: hitender
 * Date: 12/9/16 1:24 PM
 */
public class MigrateToBusinessRegistrationException extends RuntimeException {
    public MigrateToBusinessRegistrationException(String message) {
        super(message);
    }

    public MigrateToBusinessRegistrationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
