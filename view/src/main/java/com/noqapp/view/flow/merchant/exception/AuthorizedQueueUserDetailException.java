package com.noqapp.view.flow.merchant.exception;

/**
 * hitender
 * 1/19/18 12:00 AM
 */
public class AuthorizedQueueUserDetailException extends RuntimeException {

    public AuthorizedQueueUserDetailException(String message) {
        super(message);
    }

    public AuthorizedQueueUserDetailException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
