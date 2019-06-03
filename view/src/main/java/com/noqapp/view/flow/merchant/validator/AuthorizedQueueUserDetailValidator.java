package com.noqapp.view.flow.merchant.validator;

import com.noqapp.domain.flow.AuthorizedQueueUser;
import com.noqapp.view.controller.access.LandingController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 1/19/18 4:06 PM
 */
@Component
public class AuthorizedQueueUserDetailValidator {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizedQueueUserDetailValidator.class);

    @SuppressWarnings("unused")
    public String validateQueueUserDetails(AuthorizedQueueUser authorizedQueueUser, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (authorizedQueueUser.getInterests().length == 0) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("authorizedQueueUser.selectAll")
                    .defaultText("No stores selected. Please select at least one store to continue.")
                    .build());

            status = "failure";
        }

        if (authorizedQueueUser.maxSelectedStore() > authorizedQueueUser.getQueueLimit()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("authorizedQueueUser.selectAll")
                    .defaultText("Authorized store management has reach max limit of "
                        + authorizedQueueUser.getQueueLimit()
                        + " stores. Please select fewer than "
                        + authorizedQueueUser.getQueueLimit()
                        + " stores.")
                    .build());

            status = "failure";
        }

        return status;
    }
}
