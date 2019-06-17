package com.noqapp.view.flow.merchant.exception;

/**
 * User: hitender
 * Date: 2019-06-18 01:36
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
