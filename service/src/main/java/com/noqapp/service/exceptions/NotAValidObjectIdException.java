package com.noqapp.service.exceptions;

/**
 * hitender
 * 6/14/21 2:44 PM
 */
public class NotAValidObjectIdException extends RuntimeException {

    public NotAValidObjectIdException(String message) {
        super(message);
    }

    public NotAValidObjectIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
