package com.noqapp.view.flow.merchant.validator;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.PublishArticleForm;
import com.noqapp.view.form.PublishJobForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 12/28/20 9:13 PM
 */
@Component
public class PublishJobValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PublishJobValidator.class);

    public String validate(PublishJobForm publishJobForm, MessageContext messageContext) {
        LOG.info("Validate article title={}", publishJobForm.getTitle());
        String status = LandingController.SUCCESS;

        if (null == publishJobForm.getTitle() || StringUtils.isBlank(publishJobForm.getTitle())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("title")
                    .defaultText("Title cannot be empty")
                    .build());
            status = "failure";
        }

        if (null == publishJobForm.getDescription() || StringUtils.isBlank(publishJobForm.getDescription())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("description")
                    .defaultText("Description cannot be empty")
                    .build());
            status = "failure";
        } else if (publishJobForm.getDescription().length() < 200) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("description")
                    .defaultText("Description length minimum 200 characters")
                    .build());
            status = "failure";
        }

        return status;
    }
}
