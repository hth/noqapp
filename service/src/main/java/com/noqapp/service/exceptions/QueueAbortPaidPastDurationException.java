package com.noqapp.service.exceptions;

/**
 * hitender
 * 2/21/20 8:17 PM
 */
public class QueueAbortPaidPastDurationException extends RuntimeException {
    public QueueAbortPaidPastDurationException(String message) {
        super(message);
    }

    public QueueAbortPaidPastDurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
