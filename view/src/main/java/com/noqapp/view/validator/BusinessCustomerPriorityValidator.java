package com.noqapp.view.validator;

import com.noqapp.repository.BusinessCustomerPriorityManager;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.view.form.business.AverageHandlingForm;
import com.noqapp.view.form.business.BusinessCustomerPriorityForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * hitender
 * 7/10/20 11:45 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class BusinessCustomerPriorityValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCustomerPriorityValidator.class);

    private BusinessCustomerPriorityManager businessCustomerPriorityManager;

    @Autowired
    public BusinessCustomerPriorityValidator(BusinessCustomerPriorityManager businessCustomerPriorityManager) {
        this.businessCustomerPriorityManager = businessCustomerPriorityManager;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "priorityName", "field.required", new Object[]{"Priority Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "priorityLevel", "field.required", new Object[]{"Priority Level"});

        if (!errors.hasErrors()) {
            BusinessCustomerPriorityForm businessCustomerPriorityForm = (BusinessCustomerPriorityForm) target;
            if (businessCustomerPriorityManager.existPriorityCode(businessCustomerPriorityForm.getBizNameId(), businessCustomerPriorityForm.getPriorityLevel())) {
                errors.rejectValue("priorityLevel",
                    "record.exists",
                    new Object[]{"Priority Level " + businessCustomerPriorityForm.getPriorityLevel()},
                    "Priority Level already exists");
            }
        }
    }

    public void validatePriorityAccess(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "priorityAccess", "field.required", new Object[]{"Priority Access"});
    }
}
