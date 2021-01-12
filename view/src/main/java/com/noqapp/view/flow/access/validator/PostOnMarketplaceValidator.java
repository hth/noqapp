package com.noqapp.view.flow.access.validator;

import com.noqapp.domain.market.PropertyEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.PostOnMarketplaceForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 1/10/21 12:02 PM
 */
@Component
public class PostOnMarketplaceValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PostOnMarketplaceValidator.class);

    @Autowired
    public PostOnMarketplaceValidator() {
    }

    public String validateStartOfMarketplace(PostOnMarketplaceForm postOnMarketplaceForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (null == postOnMarketplaceForm.getBusinessType()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("businessType")
                    .defaultText("Posting for cannot be empty")
                    .build());
            status = "failure";
        }

        return status;
    }

    public String validateTitleDescription(PostOnMarketplaceForm postOnMarketplaceForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(postOnMarketplaceForm.getMarketplace().getTitle())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.title")
                    .defaultText("Title cannot be empty")
                    .build());
            status = "failure";
        } else if (postOnMarketplaceForm.getMarketplace().getTitle().length() > 40) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.title")
                    .defaultText("Title cannot exceed 40 characters")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(postOnMarketplaceForm.getMarketplace().getDescription())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.description")
                    .defaultText("Description cannot be empty")
                    .build());
            status = "failure";
        } else if (postOnMarketplaceForm.getMarketplace().getTitle().length() > 200) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.description")
                    .defaultText("Description cannot exceed 200 characters")
                    .build());
            status = "failure";
        }

        return status;
    }

    public String validateReadyToPostMarketplace(PostOnMarketplaceForm postOnMarketplaceForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (BusinessTypeEnum.PR == postOnMarketplaceForm.getBusinessType()) {
            PropertyEntity property = (PropertyEntity) postOnMarketplaceForm.getMarketplace();
            if (property.getBedroom() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.bedroom")
                        .defaultText("At least 1 Bedroom")
                        .build());
                status = "failure";
            }

            if (property.getBathroom() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.bathroom")
                        .defaultText("At least 1 Bathroom")
                        .build());
                status = "failure";
            }

            if (property.getCarpetArea() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.carpetArea")
                        .defaultText("Please provide carpet area")
                        .build());
                status = "failure";
            }

            if (property.getProductPrice() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.productPrice")
                        .defaultText("Please provide Rent per Month")
                        .build());
                status = "failure";
            }
        }

        if (StringUtils.isBlank(postOnMarketplaceForm.getMarketplace().getAddress())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.address")
                    .defaultText("Please provide rental address where rental unit is located. Address stays private.")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(postOnMarketplaceForm.getMarketplace().getCity())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.city")
                    .defaultText("Please provide city/area where rental unit is located")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(postOnMarketplaceForm.getMarketplace().getTown())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.town")
                    .defaultText("Please provide town/locality/sector where rental unit is located")
                    .build());
            status = "failure";
        }

        return status;
    }
}
