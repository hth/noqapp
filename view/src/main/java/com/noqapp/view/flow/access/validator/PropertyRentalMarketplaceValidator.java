package com.noqapp.view.flow.access.validator;

import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.marketplace.PropertyRentalMarketplaceForm;

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
public class PropertyRentalMarketplaceValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyRentalMarketplaceValidator.class);

    @Autowired
    public PropertyRentalMarketplaceValidator() {
    }

    public String validateStartOfMarketplace(PropertyRentalMarketplaceForm marketplaceForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (null == marketplaceForm.getBusinessType()) {
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

    public String validateTitleDescription(PropertyRentalMarketplaceForm marketplaceForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(marketplaceForm.getMarketplace().getTitle())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.title")
                    .defaultText("Title cannot be empty")
                    .build());
            status = "failure";
        } else if (marketplaceForm.getMarketplace().getTitle().length() > 40) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.title")
                    .defaultText("Title cannot exceed 40 characters")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(marketplaceForm.getMarketplace().getDescription())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.description")
                    .defaultText("Description cannot be empty")
                    .build());
            status = "failure";
        } else if (marketplaceForm.getMarketplace().getTitle().length() > 200) {
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

    public String validateReadyToPostMarketplace(PropertyRentalMarketplaceForm marketplaceForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (BusinessTypeEnum.PR == marketplaceForm.getBusinessType()) {
            PropertyRentalEntity propertyRental = (PropertyRentalEntity) marketplaceForm.getMarketplace();
            if (propertyRental.getBedroom() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.bedroom")
                        .defaultText("At least 1 Bedroom")
                        .build());
                status = "failure";
            }

            if (propertyRental.getBathroom() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.bathroom")
                        .defaultText("At least 1 Bathroom")
                        .build());
                status = "failure";
            }

            if (propertyRental.getCarpetArea() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.carpetArea")
                        .defaultText("Please provide carpet area")
                        .build());
                status = "failure";
            }

            if (propertyRental.getProductPrice() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.productPrice")
                        .defaultText("Please provide Rent per Month")
                        .build());
                status = "failure";
            }
        }

        if (StringUtils.isBlank(marketplaceForm.getMarketplace().getAddress())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.address")
                    .defaultText("Please provide rental address where rental unit is located. Address stays private.")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(marketplaceForm.getMarketplace().getCity())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.city")
                    .defaultText("Please provide city/area where rental unit is located")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(marketplaceForm.getMarketplace().getTown())) {
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
