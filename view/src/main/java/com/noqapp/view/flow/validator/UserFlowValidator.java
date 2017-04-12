package com.noqapp.view.flow.validator;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import com.noqapp.domain.flow.Register;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.service.AccountService;
import com.noqapp.service.ExternalService;
import com.noqapp.utils.CommonUtil;
import com.noqapp.utils.DateUtil;
import com.noqapp.utils.Formatter;
import com.noqapp.utils.Validate;
import com.noqapp.view.controller.access.LandingController;

/**
 * User: hitender
 * Date: 12/9/16 5:22 PM
 */
@Component
public class UserFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(UserFlowValidator.class);

    private int mailLength;
    private int nameLength;
    private int passwordLength;

    private AccountService accountService;
    private ExternalService externalService;

    @Autowired
    public UserFlowValidator(
            @Value ("${AccountRegistrationController.mailLength}")
            int mailLength,

            @Value ("${AccountRegistrationController.nameLength}")
            int nameLength,

            @Value ("${AccountRegistrationController.passwordLength}")
            int passwordLength,

            AccountService accountService,
            ExternalService externalService
    ) {
        this.mailLength = mailLength;
        this.nameLength = nameLength;
        this.passwordLength = passwordLength;
        this.accountService = accountService;
        this.externalService = externalService;
    }

    /**
     * Validate business user profile.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileSignupDetails(Register register, MessageContext messageContext) {
        LOG.info("Validate user profile signup rid={}", register.getRegisterUser().getRid());
        String status = validateUserProfileDetails(register, messageContext);

        if (StringUtils.isNotBlank(register.getRegisterUser().getBirthday()) && !DateUtil.DOB_PATTERN.matcher(register.getRegisterUser().getBirthday()).matches()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.birthday")
                            .defaultText("Age not valid. Should be digits and not more than 2 digits")
                            .build());
            status = "failure";
        }

        if (!Validate.isValidMail(register.getRegisterUser().getEmail())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.email")
                            .defaultText("Email Address provided is not valid")
                            .build());
            status = "failure";
        }

        if (register.getRegisterUser().getEmail() != null && register.getRegisterUser().getEmail().length() <= mailLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.email")
                            .defaultText("Email address has to be at least of size " + mailLength + " characters")
                            .build());
            status = "failure";
        }

        if (register.getRegisterUser().getPassword().length() < passwordLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.password")
                            .defaultText("Password minimum length of " + passwordLength + " characters")
                            .build());
            status = "failure";
        }

        if (!register.getRegisterUser().isAcceptsAgreement()) {
            if (messageContext.getAllMessages().length > 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.acceptsAgreement")
                                .defaultText("To continue, please check accept to terms")
                                .build());
                status = "failure";
            } else {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.acceptsAgreement")
                                .defaultText("To continue, please check accept to terms")
                                .build());
                status = "failure";
            }
        }

        LOG.info("Validate user profile signup rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }

    /**
     * Validate signed up user info.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(Register register, MessageContext messageContext) {
        LOG.info("Validate user profile rid={}", register.getRegisterUser().getRid());
        String status = LandingController.SUCCESS;

        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(register.getRegisterUser().getAddress()), register.getRegisterUser().getAddress());
        if (decodedAddress.isNotEmpty()) {
            register.getRegisterUser().setAddress(decodedAddress.getFormattedAddress());
            register.getRegisterUser().setCountryShortName(decodedAddress.getCountryShortName());

            LatLng latLng = CommonUtil.getLatLng(decodedAddress.getCoordinate());
            String timeZone = externalService.findTimeZone(latLng);
            register.getRegisterUser().setTimeZone(timeZone);
        }

        if (StringUtils.isBlank(register.getRegisterUser().getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(register.getRegisterUser().getFirstName()) && !Validate.isValidName(register.getRegisterUser().getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name is not a valid name: " + register.getRegisterUser().getFirstName())
                            .build());
            status = "failure";
        }

        if (register.getRegisterUser().getFirstName().length() < nameLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name minimum length of " + nameLength + " characters")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterUser().getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(register.getRegisterUser().getLastName()) && !Validate.isValidName(register.getRegisterUser().getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name is not a valid name: " + register.getRegisterUser().getLastName())
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterUser().getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.address")
                            .defaultText("Your Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterUser().getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("Your Phone number cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(register.getRegisterUser().getPhoneNotFormatted())) {
            if (!Formatter.isValidPhone(register.getRegisterUser().getPhoneNotFormatted(), register.getRegisterUser().getCountryShortName())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.phone")
                                .defaultText("Your Phone number '" + register.getRegisterUser().getPhoneNotFormatted() + "' is not valid")
                                .build());
                status = "failure";
            }
        }

        LOG.info("Validate user profile rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }
}