package com.noqapp.view.flow.access.validator;

import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.marketplace.HouseholdItemMarketplaceForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 2/24/21 4:07 PM
 */
@Component
public class HouseholdItemMarketplaceValidator {
    private static final Logger LOG = LoggerFactory.getLogger(HouseholdItemMarketplaceValidator.class);

    @Autowired
    public HouseholdItemMarketplaceValidator() {
    }

    public String validateStartOfMarketplace(HouseholdItemMarketplaceForm marketplaceForm, MessageContext messageContext) {
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

    public String validateTitleDescription(HouseholdItemMarketplaceForm marketplaceForm, MessageContext messageContext) {
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

    public String validateReadyToPostMarketplace(HouseholdItemMarketplaceForm marketplaceForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (BusinessTypeEnum.HI == marketplaceForm.getBusinessType()) {
            HouseholdItemEntity householdItem = (HouseholdItemEntity) marketplaceForm.getMarketplace();
            if (householdItem.getProductPrice() <= 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.productPrice")
                        .defaultText("Please provide List Price")
                        .build());
                status = "failure";
            }
        }

        if (StringUtils.isBlank(marketplaceForm.getMarketplace().getCity())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.city")
                    .defaultText("Please provide city/area where item is located")
                    .build());
            status = "failure";
        }

        if (StringUtils.isBlank(marketplaceForm.getMarketplace().getTown())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("marketplace.town")
                    .defaultText("Please provide town/locality/sector where item  is located")
                    .build());
            status = "failure";
        }

        return status;
    }
}
