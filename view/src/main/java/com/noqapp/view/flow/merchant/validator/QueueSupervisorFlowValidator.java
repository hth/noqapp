package com.noqapp.view.flow.merchant.validator;

import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.flow.InviteQueueSupervisor;
import com.noqapp.view.controller.access.LandingController;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 7/14/17 9:48 PM
 */
@Component
public class QueueSupervisorFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisorFlowValidator.class);

    /**
     * @param inviteQueueSupervisor
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validatePhoneNumber(InviteQueueSupervisor inviteQueueSupervisor, MessageContext messageContext) {
        LOG.info("validatePhoneNumber phone={}", inviteQueueSupervisor.getPhoneNumber());

        String status = LandingController.SUCCESS;
        if (StringUtils.isBlank(inviteQueueSupervisor.getPhoneNumber())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("phoneNumber")
                    .defaultText("Phone Number cannot be empty")
                    .build());
            status = "failure";
        } else if (!Formatter.isValidPhone(inviteQueueSupervisor.getPhoneNumber(), inviteQueueSupervisor.getCountryShortName())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("phoneNumber")
                    .defaultText("Phone Number " + inviteQueueSupervisor.getPhoneNumber() + " is not valid")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(inviteQueueSupervisor.getInviteeCode().getText())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("inviteeCode")
                    .defaultText("Invitee code cannot be empty")
                    .build());
            status = "failure";
        }

        LOG.info("validatePhoneNumber status={}", status);
        return status;
    }
}
