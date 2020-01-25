package com.noqapp.view.flow.merchant.validator;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.view.form.MerchantRegistrationForm;
import com.noqapp.view.form.ProfessionalProfileForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

@Component
public class DoctorFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(DoctorFlowValidator.class);

    @SuppressWarnings ("all")
    public String validatePhoneNumber(MerchantRegistrationForm merchantRegistration, MessageContext messageContext) {
        String status = "success";

        if (StringUtils.isBlank(merchantRegistration.getPhoneCountryCode())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("phone")
                    .defaultText("Please select a country code")
                    .build());

            status = "failure";
        }

        return status;
    }

    @SuppressWarnings ("all")
    public String validateProfessionalProfileForm(ProfessionalProfileForm form, MessageContext messageContext) {
        String status = "success";

        if (!isProfessionalProfileComplete(form)) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("practiceStart")
                    .defaultText("Education or License in professional profile cannot be empty. Please fill these up first.")
                    .build());

            status = "failure";
        }

        if (StringUtils.isBlank(form.getPracticeStart())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("practiceStart")
                    .defaultText("Practicing Since date cannot be empty")
                    .build());

            status = "failure";
        } else if (!DateUtil.DOB_PATTERN.matcher(form.getPracticeStart()).matches()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("practiceStart")
                    .defaultText("Practicing Since should be of format " + DateUtil.SDF_YYYY_MM_DD.toPattern())
                    .build());

            status = "failure";
        }

        return status;
    }

    /**
     * Validate is professional profile is complete.
     *
     * @param form
     * @return
     */
    private boolean isProfessionalProfileComplete(ProfessionalProfileForm form) {
        return !form.getLicenses().isEmpty() || !form.getEducation().isEmpty();
    }
}
