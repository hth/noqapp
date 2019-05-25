package com.noqapp.service.exceptions;

/**
 * User: hitender
 * Date: 2019-05-26 00:30
 */
public class AppointmentBookingException extends RuntimeException {
    public AppointmentBookingException(String message) {
        super(message);
    }

    public AppointmentBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
