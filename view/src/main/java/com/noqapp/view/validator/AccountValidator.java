package com.noqapp.view.validator;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.noqapp.domain.types.GenderEnum;
import com.noqapp.utils.DateUtil;
import com.noqapp.utils.Formatter;
import com.noqapp.utils.Validate;
import com.noqapp.view.form.MerchantRegistrationForm;

/**
 * User: hitender
 * Date: 11/24/16 3:36 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class AccountValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(AccountValidator.class);

    @Value ("${AccountValidator.mailLength}")
    private int mailLength;

    @Value ("${AccountValidator.nameLength}")
    private int nameLength;

    @Value ("${AccountValidator.genderLength}")
    private int genderLength;

    @Value ("${AccountValidator.countryShortNameLength}")
    private int countryShortNameLength;

    @Value ("${AccountValidator.passwordLength}")
    private int passwordLength;

    @Override
    public boolean supports(Class<?> clazz) {
        return MerchantRegistrationForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", new Object[]{"First name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required", new Object[]{"Last name"});

        /* Example of validation message: Email Address cannot be left blank. */
        /* Example of validation message: Email Address field.required. */
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "field.required", new Object[]{"Email address"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", new Object[]{"Password"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryShortName", "field.required", new Object[]{"Country Code"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "field.required", new Object[]{"Phone"});

        if (!errors.hasErrors()) {
            MerchantRegistrationForm userRegistration = (MerchantRegistrationForm) obj;
            if (StringUtils.isNotBlank(userRegistration.getCountryShortName()) && userRegistration.getCountryShortName().length() != countryShortNameLength) {
                errors.rejectValue("countryShortName",
                        "field.length",
                        new Object[]{"Country Code", countryShortNameLength},
                        "Minimum length of " + countryShortNameLength + " characters");
            } else if(!Formatter.isValidCountryCode(userRegistration.getCountryShortName())) {
                errors.rejectValue("countryShortName",
                        "field.invalid",
                        new Object[]{"Country Code", userRegistration.getCountryShortName()},
                        "Country Code is not a valid name " + userRegistration.getCountryShortName());
            }

            if (!Validate.isValidPhoneWithInternationalCode(userRegistration.getPhone())) {
                errors.rejectValue("phone",
                        "field.phone.international.invalid",
                        new Object[]{userRegistration.getPhone()},
                        "Phone number " + userRegistration.getPhone() + " should start with '+' followed by international code");
            }

            if (!Validate.isValidName(userRegistration.getFirstName())) {
                errors.rejectValue("firstName",
                        "field.invalid",
                        new Object[]{"First name", userRegistration.getFirstName()},
                        "First name is not a valid name " + userRegistration.getFirstName());
            }

            if (userRegistration.getFirstName().length() < nameLength) {
                errors.rejectValue("firstName",
                        "field.length",
                        new Object[]{"First name", nameLength},
                        "Minimum length of " + nameLength + " characters");
            }

            if (!Validate.isValidName(userRegistration.getLastName())) {
                errors.rejectValue("lastName",
                        "field.invalid",
                        new Object[]{"Last name", userRegistration.getLastName()},
                        "Last name is not a valid name " + userRegistration.getLastName());
            }

            if (!Validate.isValidMail(userRegistration.getMail())) {
                errors.rejectValue("mail",
                        "field.email.address.not.valid",
                        new Object[]{userRegistration.getMail()},
                        "Email Address provided is not valid");
            }

            if (StringUtils.isNotBlank(userRegistration.getGender())) {
                try {
                    GenderEnum.valueOf(userRegistration.getGender());
                } catch (IllegalArgumentException e) {
                    errors.rejectValue("gender",
                            "field.invalid",
                            new Object[]{"Gender", userRegistration.getGender()},
                            "Gender provided is not valid");
                }
            }

            if (userRegistration.getMail() != null && userRegistration.getMail().length() <= mailLength) {
                errors.rejectValue(
                        "mail",
                        "field.length",
                        new Object[]{"Email address", mailLength},
                        "Email address has to be at least of size " + mailLength + " characters");
            }

            if (userRegistration.getPassword().length() < passwordLength) {
                errors.rejectValue("password",
                        "field.length",
                        new Object[]{"Password", passwordLength},
                        "Minimum length of " + passwordLength + " characters");
            }

            if (StringUtils.isNotBlank(userRegistration.getBirthday()) && !DateUtil.DOB_PATTERN.matcher(userRegistration.getBirthday()).matches()) {
                errors.rejectValue("birthday",
                        "field.birthday.not.valid",
                        new Object[]{userRegistration.getBirthday()},
                        "Date format not valid " + userRegistration.getBirthday());
            }

            if (!userRegistration.isAcceptsAgreement()) {
                if (errors.hasErrors()) {
                    errors.rejectValue("acceptsAgreement",
                            "agreement.checkbox",
                            new Object[]{""},
                            "To continue, please check accept to terms");
                } else {
                    errors.rejectValue("acceptsAgreement",
                            "agreement.checkbox",
                            new Object[]{"to continue"},
                            "To continue, please check accept to terms");
                }
            }
        }
    }

    public void accountExists(Object obj, Errors errors) {
        MerchantRegistrationForm userRegistration = (MerchantRegistrationForm) obj;
        errors.rejectValue("mail",
                "phone.already.registered",
                new Object[]{userRegistration.getPhone()},
                "Account already registered with phone " + userRegistration.getPhone());
    }
}

