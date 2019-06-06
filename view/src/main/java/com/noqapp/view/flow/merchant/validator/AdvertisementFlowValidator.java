package com.noqapp.view.flow.merchant.validator;

import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.business.AdvertisementForm;

import org.apache.commons.lang3.StringUtils;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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

        if (StringUtils.isBlank(advertisementForm.getPublishDate())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("publishDate")
                    .defaultText("Please select advertisement publish date")
                    .build());

            status = "failure";
        } else {
            LocalDate publishDate = LocalDate.parse(advertisementForm.getPublishDate());
            Date publish = Date.from(publishDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date now = Date.from(Instant.now());
            if (publish.before(now)) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("publishDate")
                        .defaultText("Cannot select advertisement publish date in past")
                        .build());

                status = "failure";
            }
        }

        if (StringUtils.isBlank(advertisementForm.getEndDate())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("endDate")
                    .defaultText("Please select advertisement end date")
                    .build());

            status = "failure";
        } else {
            LocalDate endDate = LocalDate.parse(advertisementForm.getEndDate());
            Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date now = Date.from(Instant.now());
            if (end.before(now)) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("endDate")
                        .defaultText("Cannot select advertisement end date in past")
                        .build());

                status = "failure";
            }
        }

        if (StringUtils.isNotBlank(advertisementForm.getPublishDate()) && StringUtils.isNotBlank(advertisementForm.getEndDate())) {
            LocalDate publishDate = LocalDate.parse(advertisementForm.getPublishDate());
            Date publish = Date.from(publishDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            LocalDate endDate = LocalDate.parse(advertisementForm.getEndDate());
            Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            if (end.before(publish) || end.compareTo(publish) == 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("endDate")
                        .defaultText("Advertisement end date should be in future and after publish date")
                        .build());

                status = "failure";
            }
        }

        return status;
    }
}
