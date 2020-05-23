package com.noqapp.service.exceptions;

/**
 * hitender
 * 5/19/20 1:31 AM
 */
public class TokenAvailableLimitReachedException extends RuntimeException {

    public TokenAvailableLimitReachedException(String message) {
        super(message);
    }

    public TokenAvailableLimitReachedException(String message, Throwable cause) {
        super(message, cause);
    }
}
