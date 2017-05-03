package com.noqapp.view.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.noqapp.view.form.ForgotAuthenticateForm;

/**
 * User: hitender
 * Date: 5/3/17 1:03 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class ForgotAuthenticateValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ForgotAuthenticateValidator.class);

    @Value ("${AccountValidator.passwordLength}")
    private int passwordLength;

    @Override
    public boolean supports(Class<?> clazz) {
        return ForgotAuthenticateForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", new Object[]{"Password"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordSecond", "field.required", new Object[]{"Retype Password"});

        if(!errors.hasErrors()) {
            ForgotAuthenticateForm faa = (ForgotAuthenticateForm) obj;
            if (faa.getPassword().length() < passwordLength) {
                errors.rejectValue("password",
                        "field.length",
                        new Object[]{"Password", passwordLength},
                        "Password minimum length is less than 6 characters");
            }

            if (faa.getPasswordSecond().length() < passwordLength) {
                errors.rejectValue("passwordSecond",
                        "field.length",
                        new Object[]{"Retyped Password", passwordLength},
                        "Retyped Password minimum length is less than 6 characters");
            }

            if (!faa.isEqual()) {
                errors.rejectValue("password", "field.unmatched.password", "Password does not match with retyped password");
                errors.rejectValue("passwordSecond", "field.unmatched.retype.password", "Retype Password is not same as password");
            }

        }
    }
}
