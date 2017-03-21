package com.token.view.flow.validator;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import com.token.domain.flow.Register;
import com.token.domain.shared.DecodedAddress;
import com.token.service.ExternalService;
import com.token.utils.CommonUtil;

/**
 * User: hitender
 * Date: 12/9/16 6:32 PM
 */
@Component
public class BusinessFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessFlowValidator.class);

    private ExternalService externalService;

    @Autowired
    public BusinessFlowValidator(ExternalService externalService) {
        this.externalService = externalService;
    }

    /**
     * Validate business user profile.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateBusinessDetails(Register register, MessageContext messageContext) {
        LOG.info("Validate business rid={}", register.getRegisterUser().getRid());
        String status = "success";

        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(register.getRegisterBusiness().getAddress()), register.getRegisterBusiness().getAddress());
        if (decodedAddress.isNotEmpty()) {
            register.getRegisterBusiness().setAddress(decodedAddress.getFormattedAddress());
            register.getRegisterBusiness().setCountryShortName(decodedAddress.getCountryShortName());

            LatLng latLng = CommonUtil.getLatLng(decodedAddress.getCoordinate());
            String timeZone = externalService.findTimeZone(latLng);
            register.getRegisterBusiness().setTimeZone(timeZone);
        }

        if (StringUtils.isBlank(register.getRegisterBusiness().getName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessName")
                            .defaultText("Business Name cannot be empty")
                            .build());
            status = "failure";
        }

        if (null == register.getRegisterBusiness().getBusinessTypes()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessTypes")
                            .defaultText("Business Type is not selected")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterBusiness().getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessAddress")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterBusiness().getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessPhone")
                            .defaultText("Business Phone cannot be empty")
                            .build());
            status = "failure";
        }

        if (!register.getRegisterBusiness().isMultiStore()) {
            if (StringUtils.isBlank(register.getRegisterBusiness().getDisplayName())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.displayName")
                                .defaultText("Name cannot be empty. Example: Pharmacy, Driving License, Registration")
                                .build());
                status = "failure";
            }

            if (StringUtils.isBlank(register.getRegisterBusiness().getAddressStore())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.addressStore")
                                .defaultText("Stored Address cannot be empty")
                                .build());
                status = "failure";
            }

            if (StringUtils.isBlank(register.getRegisterBusiness().getPhoneStoreNotFormatted())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.phoneStore")
                                .defaultText("Store Phone cannot be empty")
                                .build());
                status = "failure";
            }

            if (register.getRegisterBusiness().getStartHourStore() == 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.startHourStore")
                                .defaultText("Store start time cannot be empty")
                                .build());
                status = "failure";
            }

            if (register.getRegisterBusiness().getEndHourStore() == 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.endHourStore")
                                .defaultText("Store close time cannot be empty")
                                .build());
                status = "failure";
            }

            if (register.getRegisterBusiness().getTokenAvailableFrom() == 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.tokenAvailableSince")
                                .defaultText("Time from Token available cannot be empty")
                                .build());
                status = "failure";
            }
        }

        LOG.info("Validate business rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }
}
