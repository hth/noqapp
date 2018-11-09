package com.noqapp.service.exceptions;

/**
 * hitender
 * 11/9/18 2:15 AM
 */
public class CSVProcessingException extends RuntimeException {

    public CSVProcessingException(String message) {
        super(message);
    }

    public CSVProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
