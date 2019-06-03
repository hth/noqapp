package com.noqapp.view.flow.merchant.validator;

import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.business.AdvertisementForm;

import org.apache.commons.lang3.StringUtils;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 2019-05-17 13:33
 */
@Component
public class AdvertisementFlowValidator {

    public String validateAdvertisement(AdvertisementForm advertisementForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(advertisementForm.getTitle())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("title")
                    .defaultText("Please select a title")
                    .build());

            status = "failure";
        }

        return status;
    }
}
