package com.noqapp.view.validator;

import com.noqapp.domain.flow.RegisterUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * hitender
 * 10/25/20 2:31 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class UserProfileValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", new Object[]{"First Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required", new Object[]{"Last Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthday", "field.required", new Object[]{"Date of Birth"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "timeZone", "field.required", new Object[]{"Time Zone"});
    }
}
