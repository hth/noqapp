package com.noqapp.service.exceptions;

/**
 * User: hitender
 * Date: 2019-06-23 16:10
 */
public class AppointmentCancellationException extends RuntimeException {
    public AppointmentCancellationException(String message) {
        super(message);
    }

    public AppointmentCancellationException(String message, Throwable cause) {
        super(message, cause);
    }
}
