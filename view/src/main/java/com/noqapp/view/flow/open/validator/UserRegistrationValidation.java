package com.noqapp.view.flow.open.validator;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.service.AccountService;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.MerchantRegistrationForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * User: hitender
 * Date: 10/06/2017 11:19 AM
 */
@Component
public class UserRegistrationValidation {
    private static final Logger LOG = LoggerFactory.getLogger(UserRegistrationValidation.class);

    @Value("${AccountValidator.mailLength}")
    private int mailLength;

    @Value("${AccountValidator.nameLength}")
    private int nameLength;

    @Value("${AccountValidator.genderLength}")
    private int genderLength;

    @Value("${AccountValidator.countryShortNameLength}")
    private int countryShortNameLength;

    @Value("${AccountValidator.passwordLength}")
    private int passwordLength;

    @Value("${AccountValidator.underAge}")
    private int underAge;

    private AccountService accountService;

    @Autowired
    public UserRegistrationValidation(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Used in validating when adding New Agent.
     */
    @SuppressWarnings("unused")
    public String validateUserDetails(MerchantRegistrationForm merchantRegistration, boolean warnWhenUnderAge, MessageContext messageContext) {
        String status = validateUserDetails(merchantRegistration, messageContext);
        if (warnWhenUnderAge) {
            if (merchantRegistration.isNotAdult()) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("birthday")
                        .defaultText("Person is under 18 years of age. Only adults allowed.")
                        .build());
                status = "failure";
            }
        }

        return status;
    }

    @SuppressWarnings("unused")
    public String validateUserDetails(MerchantRegistrationForm merchantRegistration, MessageContext messageContext) {
        LOG.info("New user signUp validation mail={}", merchantRegistration.getMail());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(merchantRegistration.getFirstName().getText())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("firstName")
                    .defaultText("First Name cannot be empty")
                    .build());
            status = "failure";
        } else {
            if (!Validate.isValidName(merchantRegistration.getFirstName().getText())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("firstName")
                        .defaultText("First Name is not a valid name " + merchantRegistration.getFirstName())
                        .build());
                status = "failure";
            }

            if (merchantRegistration.getFirstName().getText().length() < nameLength) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("firstName")
                        .defaultText("First Name minimum length of " + nameLength + " characters")
                        .build());
                status = "failure";
            }
        }

        if (StringUtils.isNotBlank(merchantRegistration.getLastName().getText())
            && !Validate.isValidName(merchantRegistration.getLastName().getText())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("lastName")
                    .defaultText("Last Name is not a valid name " + merchantRegistration.getLastName())
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(merchantRegistration.getBirthday().getText())
            && !DateUtil.DOB_PATTERN.matcher(merchantRegistration.getBirthday().getText()).matches()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("birthday")
                    .defaultText("Date format not valid " + merchantRegistration.getBirthday())
                    .build());
            status = "failure";
        } else {
            try {
                LocalDate startDate = LocalDate.parse(merchantRegistration.getBirthday().getText());
                LocalDate endDate = LocalDate.now();

                if (ChronoUnit.DAYS.between(startDate, endDate) < 0) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source("birthday")
                            .defaultText("Date is in future " + merchantRegistration.getBirthday())
                            .build());
                    status = "failure";
                }

                merchantRegistration.setNotAdult(underAge > ChronoUnit.YEARS.between(startDate, endDate));
            } catch (DateTimeParseException e) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("birthday")
                        .defaultText("Date format not valid " + merchantRegistration.getBirthday())
                        .build());
                status = "failure";
            }
        }

        if (StringUtils.isNotBlank(merchantRegistration.getGender().getText())) {
            try {
                GenderEnum.valueOf(merchantRegistration.getGender().getText());
            } catch (IllegalArgumentException e) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("gender")
                        .defaultText("Gender provided is not valid")
                        .build());
                status = "failure";
            }
        }

        status = validateMail(merchantRegistration, messageContext, status);

        if (StringUtils.isBlank(merchantRegistration.getPassword().getText())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("password")
                    .defaultText("Password cannot be empty")
                    .build());
            status = "failure";
        } else if (merchantRegistration.getPassword().getText().length() < passwordLength) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("password")
                    .defaultText("Password minimum length of " + passwordLength + " characters")
                    .build());
            status = "failure";
        }

        if (!merchantRegistration.isAcceptsAgreement()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("acceptsAgreement")
                    .defaultText("To continue, please check accept to terms")
                    .build());
            status = "failure";
        }

        UserProfileEntity userProfile = accountService.doesUserExists(merchantRegistration.getMail().getText());
        if (null != userProfile) {
            LOG.warn("Account already exists with email={}", merchantRegistration.getMail());
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("mail")
                    .defaultText("Account with this email address is already registered.")
                    .build());

            merchantRegistration.setAccountExists(true);
            status = "failure";
        }

        return status;
    }

    @SuppressWarnings("unused")
    public String passwordRecover(MerchantRegistrationForm merchantRegistration, MessageContext messageContext) {
        LOG.info("Password recovery validation mail={}", merchantRegistration.getMail());
        String status = LandingController.SUCCESS;
        status = validateMail(merchantRegistration, messageContext, status);

        if (StringUtils.isNotBlank(merchantRegistration.getCaptcha())) {
            LOG.warn("Found captcha populated");
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("mail")
                    .defaultText("Entered value does not match")
                    .build());
            status = "failure";
        }

        return status;
    }

    private String validateMail(MerchantRegistrationForm merchantRegistration, MessageContext messageContext, String status) {
        if (StringUtils.isBlank(merchantRegistration.getMail().getText())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("mail")
                    .defaultText("Mail cannot be empty")
                    .build());
            status = "failure";
        } else {
            if (!Validate.isValidMail(merchantRegistration.getMail().getText())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("mail")
                        .defaultText("Email Address provided is not valid")
                        .build());
                status = "failure";
            }

            if (merchantRegistration.getMail() != null && merchantRegistration.getMail().getText().length() <= mailLength) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("mail")
                        .defaultText("Email Address has to be at least of size " + mailLength + " characters")
                        .build());
                status = "failure";
            } else if (merchantRegistration.getMail() != null && merchantRegistration.getMail().getText().length() >= mailLength) {
                if (!CommonUtil.validateMail(merchantRegistration.getMail().getText())) {
                    LOG.warn("Failed validation mail={}", merchantRegistration.getMail());
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source("mail")
                            .defaultText("Please correct the email address")
                            .build());
                    status = "failure";
                }
            }
        }
        return status;
    }
}
