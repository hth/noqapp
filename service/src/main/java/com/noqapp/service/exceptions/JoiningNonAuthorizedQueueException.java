package com.noqapp.service.exceptions;

/**
 * hitender
 * 5/25/20 5:05 PM
 */
public class JoiningNonAuthorizedQueueException extends RuntimeException {

    public JoiningNonAuthorizedQueueException(String message) {
        super(message);
    }

    public JoiningNonAuthorizedQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
