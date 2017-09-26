package com.noqapp.view.flow.validator;

import com.noqapp.domain.shared.DecodedAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.AccountService;

/**
 * User: hitender
 * Date: 7/17/17 12:29 PM
 */
@Component
public class MigrateToBusinessProfileValidator {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateToBusinessProfileValidator.class);

    private UserFlowValidator userFlowValidator;
    private AccountService accountService;

    @Autowired
    public MigrateToBusinessProfileValidator(
            UserFlowValidator userFlowValidator,
            AccountService accountService
    ) {
        this.userFlowValidator = userFlowValidator;
        this.accountService = accountService;
    }

    /**
     * Migrating user profile to business queue supervisor profile.
     *
     * @param registerUser
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validateUserProfileSignUpDetails(RegisterUser registerUser, MessageContext messageContext) {
        String status = userFlowValidator.validateUserProfileSignUpDetails(registerUser, messageContext);

        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String qid = queueUser.getQueueUserId();

        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(registerUser.getPhoneWithCountryCode());
        if (null == userProfile) {
            /*
             * This should not happen and the possible reason could be the
             * address and phone are from different countries. Or user does not exists.
             */
            LOG.error("Could not find user with phone={} countryShortName={} qid={}",
                    registerUser.getPhoneWithCountryCode(),
                    registerUser.getCountryShortName(),
                    qid);

            if (!queueUser.getCountryShortName().equalsIgnoreCase(registerUser.getCountryShortName())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.address")
                                .defaultText("Address and Phone are not from the same country. Please update your Phone or fix Address to match.")
                                .build());

                status = "failure";
            } else {

                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.phone")
                                .defaultText("Could not find user with phone number " + registerUser.getPhoneWithCountryCode())
                                .build());

                status = "failure";
            }
        } else if (!userProfile.getQueueUserId().equalsIgnoreCase(qid)) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("User already exists with this number. Please try to recover account before proceeding.")
                            .build());
            
            status = "failure";
        }

        if (!registerUser.getFoundAddresses().isEmpty()) {
            for (String decodedAddressId : registerUser.getFoundAddresses().keySet()) {
                DecodedAddress decodedAddress = registerUser.getFoundAddresses().get(decodedAddressId);
                if (!decodedAddress.getCountryShortName().equalsIgnoreCase(registerUser.getCountryShortName())) {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source("registerUser.address")
                                    .defaultText("Address and Phone are not from the same country. Please update your Phone or fix Address to match.")
                                    .build());

                    status = "failure";
                    break;
                }
            }

        }

        return status;
    }
}
