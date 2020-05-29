package com.noqapp.service.exceptions;

/**
 * hitender
 * 5/28/20 11:45 PM
 */
public class JoiningQueuePermissionDeniedException extends RuntimeException {
    public JoiningQueuePermissionDeniedException(String message) {
        super(message);
    }

    public JoiningQueuePermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
