package com.noqapp.service.exceptions;

/**
 * hitender
 * 5/28/21 5:39 PM
 */
public class DuplicateMessageException extends RuntimeException {
    public DuplicateMessageException(String message) {
        super(message);
    }

    public DuplicateMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
