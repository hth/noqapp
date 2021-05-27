package com.noqapp.view.flow.access.exception;

/**
 * hitender
 * 5/25/21 6:32 PM
 */
public class AddPrimaryContactException extends RuntimeException {
    public AddPrimaryContactException(String message) {
        super(message);
    }

    public AddPrimaryContactException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
