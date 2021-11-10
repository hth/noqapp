package com.noqapp.view.flow.merchant.validator;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.json.JsonUserAddress;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.shared.Geocode;
import com.noqapp.service.ExternalService;
import com.noqapp.view.controller.access.LandingController;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import java.util.Map;

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
        @Value("${AccountValidator.mailLength}")
        int mailLength,

        @Value("${AccountValidator.nameLength}")
        int nameLength,

        @Value("${AccountValidator.passwordLength}")
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
     * @param source         Identify label correctly in jsp. Matches error message with input with error css.
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validateUserProfileSignUpDetails(RegisterUser registerUser, String source, MessageContext messageContext) {
        LOG.info("Validate user profile signUp qid={}", registerUser.getQueueUserId());
        String status = validateUserProfileDetails(registerUser, source, messageContext);

        if (StringUtils.isBlank(registerUser.getBirthday())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "birthday")
                    .defaultText("Data of birth cannot be empty. Should be of format yyyy-MM-dd.")
                    .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerUser.getBirthday()) && !DateUtil.DOB_PATTERN.matcher(registerUser.getBirthday()).matches()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "birthday")
                    .defaultText("Data of birth not valid. Should be of format yyyy-MM-dd.")
                    .build());
            status = "failure";
        }

        if (!Validate.isValidMail(registerUser.getEmail())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "email")
                    .defaultText("Email Address provided is not valid")
                    .build());
            status = "failure";
        }

        if (registerUser.getEmail() != null && registerUser.getEmail().length() <= mailLength) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "email")
                    .defaultText("Email address has to be at least of size " + mailLength + " characters")
                    .build());
            status = "failure";
        } else if (registerUser.getEmail() != null && registerUser.getEmail().length() >= mailLength) {
            if (!CommonUtil.validateMail(registerUser.getEmail())) {
                LOG.warn("Failed validation mail={}", registerUser.getEmail());
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "email")
                        .defaultText("Please correct the email address")
                        .build());
                status = "failure";
            }
        }

        /* Ask for password when email is not validated. */
        if (!registerUser.isEmailValidated()) {
            if (StringUtils.isBlank(registerUser.getPassword()) || registerUser.getPassword().length() < passwordLength) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "password")
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
                        .source(source + "acceptsAgreement")
                        .defaultText("To continue, please check accept to terms")
                        .build());
                status = "failure";
            } else {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "acceptsAgreement")
                        .defaultText("To continue, please check accept to terms")
                        .build());
                status = "failure";
            }
        }

        LOG.info("Validate user profile signup qid={} status={}", registerUser.getQueueUserId(), status);
        return status;
    }

    //source("registerUser.phone")
    //.code("field.phone.international.invalid")

    /**
     * Validate signed up user info.
     *
     * @param registerUser
     * @param source         Identify label correctly in jsp. Matches error message with input with error css.
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validateUserProfileDetails(RegisterUser registerUser, String source, MessageContext messageContext) {
        LOG.info("Validate user profile qid={}", registerUser.getQueueUserId());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(registerUser.getAddress())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "address")
                    .defaultText("Your Address cannot be empty")
                    .build());
            status = "failure";
        } else {
            DecodedAddress decodedAddress;
            if (registerUser.isSelectFoundAddress()) {
                /* Use Google supplied address. */
                decodedAddress = registerUser.getFoundAddresses().get(registerUser.getFoundAddressPlaceId());
                registerUser.setAddress(new ScrubbedInput(decodedAddress.getFormattedAddress()));
                registerUser.setJsonUserAddress(JsonUserAddress.populateJsonUserAddressFromDecode(decodedAddress));
            } else if (registerUser.getFoundAddresses().isEmpty() || registerUser.hasUserEnteredAddressChanged()) {
                /* This code would track if the address entered by user has changed. */
                registerUser.setPlaceHolderAddress(registerUser.getAddress());

                Geocode geocode = Geocode.newInstance(externalService.getGeocodingResults(registerUser.getAddress()), registerUser.getAddress());
                registerUser.setFoundAddresses(geocode.getFoundAddresses());
                decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);

                if (decodedAddress.isNotBlank()) {
                    if (geocode.getResults().length > 1 || geocode.isAddressMisMatch()) {
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source(source + "address")
                                .defaultText("Found other matching address(es). Please select 'Best Matching Address' or if you choose 'Your Address' then click Next.")
                                .build());
                        status = "failure";
                    }
                } else {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "address")
                            .defaultText("Failed decoding your address. Please contact support if this error persists.")
                            .build());
                    status = "failure";
                }
            } else {
                /*
                 * Since user has preferred to use the address entered by user, we take what we found and
                 * replace the other parameter with decoded address and keep the original address same.
                 */
                Map.Entry<String, DecodedAddress> entry = registerUser.getFoundAddresses().entrySet().iterator().next();
                decodedAddress = entry.getValue();
                registerUser.setJsonUserAddress(JsonUserAddress.populateJsonUserAddressFromDecode(decodedAddress, registerUser.getAddress()));
            }

            if (!registerUser.getFoundAddresses().isEmpty()) {
                for (String decodedAddressId : registerUser.getFoundAddresses().keySet()) {
                    DecodedAddress foundAddress = registerUser.getFoundAddresses().get(decodedAddressId);
                    if (!foundAddress.getCountryShortName().equalsIgnoreCase(registerUser.getCountryShortName())) {
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source(source + "address")
                                .defaultText("Address and Phone are not from the same country. Please update your Phone or fix Address to match.")
                                .build());

                        status = "failure";
                        break;
                    }
                }
            }

            /* Make sure you are not over writing country short name when phone is already validated. */
            if (registerUser.isPhoneValidated()) {
                if (!registerUser.getCountryShortName().equalsIgnoreCase(decodedAddress.getCountryShortName())) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "address")
                            .defaultText("Your address does not match with the country of registered phone number. " +
                                "Phone is registered to " + registerUser.getCountryShortName())
                            .build());
                    status = "failure";
                }
            }

            if (status.equalsIgnoreCase(LandingController.SUCCESS)) {
                /* Reset to raw format before updating to new address and countryShortName. */
                try {
                    if (StringUtils.isBlank(registerUser.getPhoneNotFormatted())) {
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source(source + "phone")
                                .defaultText("Your Phone number cannot be empty")
                                .build());
                        status = "failure";
                        return status;
                    } else {
                        String updatedPhone = Formatter.resetPhoneToRawFormat(registerUser.getPhone(), registerUser.getCountryShortName());
                        registerUser.setPhone(new ScrubbedInput(updatedPhone));
                    }
                } catch (Exception e) {
                    LOG.error("Failed parsing phone number reason={}", e.getLocalizedMessage(), e);
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "phone")
                            .defaultText("Your Phone number '" + registerUser.getPhoneAsIs() + "' is not valid")
                            .build());
                    status = "failure";
                    return status;
                }

                /* No need to call Lat and Lng when validation has already failed. */
                if (status.equalsIgnoreCase(LandingController.SUCCESS)) {
                    registerUser.setCountryShortName(new ScrubbedInput(decodedAddress.getCountryShortName()));

                    LatLng latLng = CommonUtil.getLatLng(decodedAddress.getCoordinate());
                    String timeZone = externalService.findTimeZone(latLng);
                    registerUser.setTimeZone(new ScrubbedInput(timeZone));
                }
            }
        }

        if (StringUtils.isBlank(registerUser.getFirstName())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "firstName")
                    .defaultText("First name cannot be empty")
                    .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerUser.getFirstName()) && !Validate.isValidName(registerUser.getFirstName())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "firstName")
                    .defaultText("First name is not a valid name: " + registerUser.getFirstName())
                    .build());
            status = "failure";
        }

        if (registerUser.getFirstName().length() < nameLength) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "firstName")
                    .defaultText("First name minimum length of " + nameLength + " characters")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(registerUser.getLastName())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "lastName")
                    .defaultText("Last name cannot be empty")
                    .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerUser.getLastName()) && !Validate.isValidName(registerUser.getLastName())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "lastName")
                    .defaultText("Last name is not a valid name: " + registerUser.getLastName())
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(registerUser.getPhoneNotFormatted())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "phone")
                    .defaultText("Your Phone number cannot be empty")
                    .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerUser.getPhoneNotFormatted())) {
            if (!Formatter.isValidPhone(registerUser.getPhoneNotFormatted(), registerUser.getCountryShortName())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "phone")
                        .defaultText("Your Phone number '" + registerUser.getPhoneNotFormatted() + "' is not valid")
                        .build());
                status = "failure";
            }
        }

        LOG.info("Validate user profile qid={} status={}", registerUser.getQueueUserId(), status);
        return status;
    }
}
