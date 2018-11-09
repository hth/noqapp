package com.noqapp.service.exceptions;

/**
 * hitender
 * 11/9/18 2:57 PM
 */
public class CSVParsingException extends RuntimeException {

    public CSVParsingException(String message) {
        super(message);
    }

    public CSVParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}