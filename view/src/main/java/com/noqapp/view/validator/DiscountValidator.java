package com.noqapp.view.validator;

import com.noqapp.view.form.business.DiscountForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 2019-06-10 00:32
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class DiscountValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(DiscountValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return DiscountForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "discountName", "field.required", new Object[]{"Discount Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "discountDescription", "field.required", new Object[]{"Discount Description"});

        if (!errors.hasErrors()) {
            DiscountForm discountForm = (DiscountForm) target;
            if (discountForm.getDiscountType() == null) {
                errors.rejectValue("discountType",
                    "field.required",
                    new Object[]{"Discount Type"},
                    "Please set discount type");
            }

            if (discountForm.getDiscountAmount() <= 0) {
                errors.rejectValue("discountAmount",
                    "field.amount",
                    new Object[]{"Discount amount"},
                    "Discount amount should be greater than 0");
            }
        }
    }
}
