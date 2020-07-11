package com.noqapp.view.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * hitender
 * 7/10/20 11:45 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class BusinessCustomerPriorityValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(CustomTextToSpeechValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "availableTokenCount", "field.required", new Object[]{"Issue Limited Tokens"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedDayOfWeek", "field.required", new Object[]{"Default Day Of Week"});
    }
}
