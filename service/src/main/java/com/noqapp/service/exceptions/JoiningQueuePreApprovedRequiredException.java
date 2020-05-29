package com.noqapp.service.exceptions;

/**
 * This exception happens when business requires BusinessCustomer populated. When not populated user is shown this exception.
 * hitender
 * 5/8/20 3:32 PM
 */
public class JoiningQueuePreApprovedRequiredException extends RuntimeException {
    public JoiningQueuePreApprovedRequiredException(String message) {
        super(message);
    }

    public JoiningQueuePreApprovedRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
