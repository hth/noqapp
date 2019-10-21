package com.noqapp.view.flow.merchant.exception;

/**
 * User: hitender
 * Date: 10/21/19 3:54 AM
 */
public class SurveyException extends RuntimeException {
    public SurveyException(String message) {
        super(message);
    }

    public SurveyException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
