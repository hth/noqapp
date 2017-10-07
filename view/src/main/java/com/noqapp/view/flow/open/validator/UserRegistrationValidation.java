package com.noqapp.view.flow.open.validator;

import com.noqapp.domain.flow.Register;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.flow.merchant.validator.MigrateToBusinessProfileValidator;
import com.noqapp.view.form.MerchantRegistrationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 10/06/2017 11:19 AM
 */
@Component
public class UserRegistrationValidation {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateToBusinessProfileValidator.class);

    public String validateUserDetails(MerchantRegistrationForm merchantRegistrationForm, MessageContext messageContext) {
        LOG.info("validatePhoneNumber phone={}", merchantRegistrationForm.getPhone());

        String status = LandingController.SUCCESS;

        return status;
    }
}
