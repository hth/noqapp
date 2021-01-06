package com.noqapp.view.flow.merchant.validator;

import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.PublishArticleForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 2019-01-02 15:03
 */
@Component
public class PublishArticleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PublishArticleValidator.class);

    public String validate(PublishArticleForm publishArticleForm, MessageContext messageContext) {
        LOG.info("Validate article title={}", publishArticleForm.getTitle());
        String status = LandingController.SUCCESS;

        if (null == publishArticleForm.getTitle() || StringUtils.isBlank(publishArticleForm.getTitle())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("articleTitle")
                    .defaultText("Title cannot be empty")
                    .build());
            status = "failure";
        }

        if (null == publishArticleForm.getDescription() || StringUtils.isBlank(publishArticleForm.getDescription())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("article")
                    .defaultText("Description cannot be empty")
                    .build());
            status = "failure";
        } else if (publishArticleForm.getDescription().length() < 1000) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("article")
                    .defaultText("Description length minimum 1000 characters")
                    .build());
            status = "failure";
        }

        return status;
    }
}
