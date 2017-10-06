package com.noqapp.view.flow.merchant.exception;

/**
 * User: hitender
 * Date: 7/17/17 11:21 PM
 */
public class MigrateToBusinessProfileException extends RuntimeException {
    public MigrateToBusinessProfileException(String message) {
        super(message);
    }

    public MigrateToBusinessProfileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
