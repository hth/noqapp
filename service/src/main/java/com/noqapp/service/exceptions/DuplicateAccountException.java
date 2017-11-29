package com.noqapp.service.exceptions;

/**
 * hitender
 * 11/29/17 9:42 AM
 */
public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException(String message) {
        super(message);
    }

    public DuplicateAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
