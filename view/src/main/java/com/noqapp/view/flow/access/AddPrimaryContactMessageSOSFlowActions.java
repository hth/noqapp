package com.noqapp.view.flow.access;

import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.AccountService;
import com.noqapp.service.UserProfilePreferenceService;
import com.noqapp.view.flow.access.exception.AddPrimaryContactException;
import com.noqapp.view.form.AddPrimaryContactMessageSOSForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 5/24/21 5:48 PM
 */
@Component
public class AddPrimaryContactMessageSOSFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AddPrimaryContactMessageSOSFlowActions.class);

    private AccountService accountService;
    private UserProfilePreferenceService userProfilePreferenceService;

    @Autowired
    public AddPrimaryContactMessageSOSFlowActions(
        AccountService accountService,
        UserProfilePreferenceService userProfilePreferenceService
    ) {
        this.accountService = accountService;
        this.userProfilePreferenceService = userProfilePreferenceService;
    }

    public AddPrimaryContactMessageSOSForm create() {
        return new AddPrimaryContactMessageSOSForm();
    }

    @SuppressWarnings ("unused")
    public AddPrimaryContactMessageSOSForm completeAdditionOfPrimaryContact(
        AddPrimaryContactMessageSOSForm addPrimaryContactMessageSOSForm,
        MessageContext messageContext
    ) {
        String internationalFormat;
        try {
            internationalFormat = Formatter.phoneInternationalFormat(
                addPrimaryContactMessageSOSForm.getPhoneNumber(),
                addPrimaryContactMessageSOSForm.getCountryShortName());

            LOG.info("International phone number={}", internationalFormat);
        } catch (Exception e) {
            LOG.error("Failed parsing international format phone={} countryShortName={}",
                addPrimaryContactMessageSOSForm.getPhoneNumber(),
                addPrimaryContactMessageSOSForm.getCountryShortName());

            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("addPrimaryContactMessageSOSForm.phoneNumber")
                    .defaultText("Phone number " + addPrimaryContactMessageSOSForm.getPhoneNumber() + " not valid.")
                    .build());

            throw new AddPrimaryContactException("Phone number not valid");
        }

        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(Formatter.phoneCleanup(internationalFormat));
        if (null == userProfile) {
            /* Find based on invitee code, in case the numbers don't match. */
            userProfile = accountService.findProfileByInviteCode(addPrimaryContactMessageSOSForm.getInviteeCode().getText());
        }

        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userProfile.getQueueUserId().equalsIgnoreCase(queueUser.getQueueUserId())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("addPrimaryContactMessageSOSForm.phoneNumber")
                    .defaultText("Cannot add self for SOS receiving.")
                    .build());

            throw new AddPrimaryContactException("Cannot add self");
        }

        if (!userProfile.getInviteCode().equals(addPrimaryContactMessageSOSForm.getInviteeCode().getText())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("addPrimaryContactMessageSOSForm.phoneNumber")
                    .defaultText("User of phone number "
                        + addPrimaryContactMessageSOSForm.getPhoneNumber()
                        + " does not exists or Invitee code does not match.")
                    .build());

            throw new AddPrimaryContactException("User does not exists or Invitee code does not match");
        }

        UserPreferenceEntity userPreference = userProfilePreferenceService.findByQueueUserId(queueUser.getQueueUserId());
        if (userPreference.getSosReceiverQids().contains(userProfile.getQueueUserId())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("addPrimaryContactMessageSOSForm.phoneNumber")
                    .defaultText("This person is already registered as primary contact for receiving SOS message.")
                    .build());

            throw new AddPrimaryContactException("Cannot add self");
        }
        userPreference.addSosReceiverQid(userProfile.getQueueUserId());
        userProfilePreferenceService.save(userPreference);

        return addPrimaryContactMessageSOSForm;
    }
}
