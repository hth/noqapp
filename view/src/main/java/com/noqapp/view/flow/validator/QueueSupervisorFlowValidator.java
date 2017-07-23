package com.noqapp.view.flow.validator;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import com.noqapp.domain.flow.InviteQueueSupervisor;
import com.noqapp.utils.Formatter;

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

        String status = "success";
        if (StringUtils.isBlank(inviteQueueSupervisor.getPhoneNumber())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("inviteQueueSupervisor.phoneNumber")
                            .defaultText("Phone Number cannot be empty")
                            .build());
            status = "failure";
        }

        if (!Formatter.isValidPhone(inviteQueueSupervisor.getPhoneNumber(), inviteQueueSupervisor.getCountryShortName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("inviteQueueSupervisor.phoneNumber")
                            .defaultText("Phone Number " + inviteQueueSupervisor.getPhoneNumber() + " is not valid")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(inviteQueueSupervisor.getInviteeCode())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("inviteQueueSupervisor.inviteeCode")
                            .defaultText("Invitee code cannot be empty")
                            .build());
            status = "failure";
        }

        LOG.info("validatePhoneNumber status={}", status);
        return status;
    }
}
