package com.noqapp.view.flow.access.validator;

import com.noqapp.common.utils.Formatter;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.AddPrimaryContactMessageSOSForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 5/25/21 3:59 PM
 */
@Component
public class PrimaryContactFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PrimaryContactFlowValidator.class);

    /**
     * @param addPrimaryContactMessageSOSForm
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validatePhoneNumber(AddPrimaryContactMessageSOSForm addPrimaryContactMessageSOSForm, MessageContext messageContext) {
        LOG.info("validatePhoneNumber phone={}", addPrimaryContactMessageSOSForm.getPhoneNumber());

        String status = LandingController.SUCCESS;
        if (StringUtils.isBlank(addPrimaryContactMessageSOSForm.getPhoneNumber())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("phoneNumber")
                    .defaultText("Phone Number cannot be empty")
                    .build());
            status = "failure";
        } else if (!Formatter.isValidPhone(addPrimaryContactMessageSOSForm.getPhoneNumber(), addPrimaryContactMessageSOSForm.getCountryShortName())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("phoneNumber")
                    .defaultText("Phone Number " + addPrimaryContactMessageSOSForm.getPhoneNumber() + " is not valid")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(addPrimaryContactMessageSOSForm.getInviteeCode().getText())) {
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
