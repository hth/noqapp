package com.noqapp.view.flow.access.validator;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.marketplace.PropertyRentalMarketplaceForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

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

    @SuppressWarnings("unused")
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
        } else if (BusinessTypeEnum.PR != marketplaceForm.getBusinessType()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("businessType")
                    .defaultText("Please select " + BusinessTypeEnum.PR.getDescription() + " to continue")
                    .build());
            status = "failure";
        }

        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (queueUser.hasNonOperationalEmailDomain()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("businessType")
                    .defaultText("Your email address is not verified to post here. Please change your mail address or contact support.")
                    .build());
            status = "failure";
        }

        if (null != marketplaceForm.getMarketplace()) {
            if (null != marketplaceForm.getMarketplace().getPublishUntil() && marketplaceForm.getMarketplace().isPostingExpired()) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("businessType")
                        .defaultText("Cannot edit expired post")
                        .build());
                status = "failure";
            }
        }

        return status;
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public String validateReadyToPostMarketplace(PropertyRentalMarketplaceForm marketplaceForm, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

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

            if (StringUtils.isBlank(propertyRental.getRentalAvailableDay())
                && !DateUtil.DOB_PATTERN.matcher(propertyRental.getRentalAvailableDay()).matches()) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("marketplace.rentalAvailableDay")
                        .defaultText("Date format not valid " + propertyRental.getRentalAvailableDay())
                        .build());
                status = "failure";
            } else {
                try {
                    LocalDate startDate = LocalDate.parse(propertyRental.getRentalAvailableDay());
                    LocalDate endDate = LocalDate.now();

                    if (ChronoUnit.DAYS.between(startDate, endDate) > 0) {
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source("marketplace.rentalAvailableDay")
                                .defaultText("Date is in past " + propertyRental.getRentalAvailableDay())
                                .build());
                        status = "failure";
                    }

                } catch (DateTimeParseException e) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source("marketplace.rentalAvailableDay")
                            .defaultText("Date format not valid " + propertyRental.getRentalAvailableDay())
                            .build());
                    status = "failure";
                }
            }

            if (!Validate.isValidPrice(marketplaceForm.getListPrice())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("listPrice")
                        .defaultText("Please correct Rent per Month to match format ####.## and without ','")
                        .build());
                status = "failure";
                return status;
            }

            if (marketplaceForm.isListPriceValid()) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("listPrice")
                        .defaultText("Rent per Month should be greater than zero")
                        .build());
                status = "failure";
            }
        }

        return status;
    }
}
