package com.noqapp.view.flow.validator;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.shared.DecodedAddress;
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

    private ExternalService externalService;

    @Autowired
    public UserFlowValidator(
            @Value ("${AccountValidator.mailLength}")
            int mailLength,

            @Value ("${AccountValidator.nameLength}")
            int nameLength,

            @Value ("${AccountValidator.passwordLength}")
            int passwordLength,

            ExternalService externalService
    ) {
        this.mailLength = mailLength;
        this.nameLength = nameLength;
        this.passwordLength = passwordLength;
        this.externalService = externalService;
    }

    /**
     * Validate business user profile.
     *
     * @param registerUser
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileSignupDetails(RegisterUser registerUser, MessageContext messageContext) {
        LOG.info("Validate user profile signup rid={}", registerUser.getRid());
        String status = validateUserProfileDetails(registerUser, messageContext);

        if (StringUtils.isBlank(registerUser.getBirthday())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.birthday")
                            .defaultText("Data of birth cannot be empty. Should be of format yyyy-MM-dd.")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerUser.getBirthday()) && !DateUtil.DOB_PATTERN.matcher(registerUser.getBirthday()).matches()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.birthday")
                            .defaultText("Data of birth not valid. Should be of format yyyy-MM-dd.")
                            .build());
            status = "failure";
        }

        if (!Validate.isValidMail(registerUser.getEmail())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.email")
                            .defaultText("Email Address provided is not valid")
                            .build());
            status = "failure";
        }

        if (registerUser.getEmail() != null && registerUser.getEmail().length() <= mailLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.email")
                            .defaultText("Email address has to be at least of size " + mailLength + " characters")
                            .build());
            status = "failure";
        }

        /* Ask for password when email is not validated. */
        if (!registerUser.isEmailValidated()) {
            if (StringUtils.isBlank(registerUser.getPassword()) || registerUser.getPassword().length() < passwordLength) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.password")
                                .defaultText("Password minimum length of " + passwordLength + " characters")
                                .build());
                status = "failure";
            }
        }

        if (!registerUser.isAcceptsAgreement()) {
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

        LOG.info("Validate user profile signup rid={} status={}", registerUser.getRid(), status);
        return status;
    }

    //source("registerUser.phone")
    //.code("field.phone.international.invalid")

    /**
     * Validate signed up user info.
     *
     * @param registerUser
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(RegisterUser registerUser, MessageContext messageContext) {
        LOG.info("Validate user profile rid={}", registerUser.getRid());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(registerUser.getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.address")
                            .defaultText("Your Address cannot be empty")
                            .build());
            status = "failure";
        } else {
            GeocodingResult[] geocodingResults = externalService.getGeocodingResults(registerUser.getAddress());
            if (null != geocodingResults) {
                DecodedAddress decodedAddress = DecodedAddress.newInstance(geocodingResults, registerUser.getAddress());
                if (decodedAddress.isNotEmpty()) {
                    /* Make sure you are not over writing country short name when phone is already validated. */
                    if (registerUser.isPhoneValidated()) {
                        if (!registerUser.getCountryShortName().equalsIgnoreCase(decodedAddress.getCountryShortName())) {
                            messageContext.addMessage(
                                    new MessageBuilder()
                                            .error()
                                            .source("registerUser.address")
                                            .defaultText("Your address does not match with the country of registered phone number. " +
                                                    "Phone is registered to " + registerUser.getCountryShortName())
                                            .build());
                            status = "failure";
                        }
                    }

                    if (status.equalsIgnoreCase(LandingController.SUCCESS)) {
                        /* Reset to raw format before updating to new address and countryShortName. */
                        String updatedPhone = Formatter.resetPhoneToRawFormat(registerUser.getPhone(), registerUser.getCountryShortName());
                        registerUser.setPhone(updatedPhone);

                        registerUser.setAddress(decodedAddress.getFormattedAddress());
                        registerUser.setCountryShortName(decodedAddress.getCountryShortName());

                        LatLng latLng = CommonUtil.getLatLng(decodedAddress.getCoordinate());
                        String timeZone = externalService.findTimeZone(latLng);
                        registerUser.setTimeZone(timeZone);
                    }
                }
            }
        }

        if (StringUtils.isBlank(registerUser.getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerUser.getFirstName()) && !Validate.isValidName(registerUser.getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name is not a valid name: " + registerUser.getFirstName())
                            .build());
            status = "failure";
        }

        if (registerUser.getFirstName().length() < nameLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name minimum length of " + nameLength + " characters")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(registerUser.getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerUser.getLastName()) && !Validate.isValidName(registerUser.getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name is not a valid name: " + registerUser.getLastName())
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(registerUser.getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("Your Phone number cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerUser.getPhoneNotFormatted())) {
            if (!Formatter.isValidPhone(registerUser.getPhoneNotFormatted(), registerUser.getCountryShortName())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.phone")
                                .defaultText("Your Phone number '" + registerUser.getPhoneNotFormatted() + "' is not valid")
                                .build());
                status = "failure";
            }
        }

        LOG.info("Validate user profile rid={} status={}", registerUser.getRid(), status);
        return status;
    }
}