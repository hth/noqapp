package com.noqapp.view.flow.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.site.TokenUser;
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

    public String validateUserProfileSignupDetails(RegisterUser registerUser, MessageContext messageContext) {
        String status = userFlowValidator.validateUserProfileSignupDetails(registerUser, messageContext);

        TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = tokenUser.getRid();

        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(registerUser.getPhoneWithCountryCode());
        if (null == userProfile) {
            /* This should never happen. */
            LOG.error("Could not find user with phone={} countryShortName={} rid={}",
                    registerUser.getPhoneWithCountryCode(),
                    registerUser.getCountryShortName(),
                    rid);

            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("Could not find user with phone number " + registerUser.getPhoneWithCountryCode())
                            .build());

            status = "failure";
        } else if (!userProfile.getReceiptUserId().equalsIgnoreCase(rid)) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("User already exists with this number. Please try to recover account before proceeding.")
                            .build());
            
            status = "failure";
        }

        return status;
    }
}
