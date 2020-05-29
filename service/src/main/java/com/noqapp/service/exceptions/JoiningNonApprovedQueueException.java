package com.noqapp.service.exceptions;

/**
 * This exception happens when user approved for one queue tries to join another queue not approved for.
 * hitender
 * 5/25/20 5:05 PM
 */
public class JoiningNonApprovedQueueException extends RuntimeException {

    public JoiningNonApprovedQueueException(String message) {
        super(message);
    }

    public JoiningNonApprovedQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
