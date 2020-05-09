package com.noqapp.service.exceptions;

/**
 * hitender
 * 5/8/20 3:32 PM
 */
public class AuthorizedUserCanJoinQueueException extends RuntimeException {
    public AuthorizedUserCanJoinQueueException(String message) {
        super(message);
    }

    public AuthorizedUserCanJoinQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
