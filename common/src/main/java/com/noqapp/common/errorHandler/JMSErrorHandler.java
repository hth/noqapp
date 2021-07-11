package com.noqapp.common.errorHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ErrorHandler;

/**
 * hitender
 * 7/8/21 4:39 PM
 */
public class JMSErrorHandler implements ErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(JMSErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        LOG.error("Failed JMS Message : {}", t.getMessage());
    }
}
