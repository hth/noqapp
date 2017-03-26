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
import com.token.domain.flow.RegisterBusiness;
import com.token.domain.shared.DecodedAddress;
import com.token.service.BizService;
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
    private BizService bizService;

    @Autowired
    public BusinessFlowValidator(
            ExternalService externalService,
            BizService bizService
    ) {
        this.externalService = externalService;
        this.bizService = bizService;
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

        final RegisterBusiness registerBusiness = register.getRegisterBusiness();

        if (StringUtils.isBlank(registerBusiness.getName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessName")
                            .defaultText("Business Name cannot be empty")
                            .build());
            status = "failure";
        }

        if (null == registerBusiness.getBusinessTypes()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessTypes")
                            .defaultText("Business Type is not selected")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(registerBusiness.getAddress())) {
            DecodedAddress decodedAddress = DecodedAddress.newInstance(
                    externalService.getGeocodingResults(registerBusiness.getAddress()),
                    registerBusiness.getAddress());
            
            if (decodedAddress.isNotEmpty()) {
                registerBusiness.setAddress(decodedAddress.getFormattedAddress());
                registerBusiness.setCountryShortName(decodedAddress.getCountryShortName());

                LatLng latLng = CommonUtil.getLatLng(decodedAddress.getCoordinate());
                String timeZone = externalService.findTimeZone(latLng);
                registerBusiness.setTimeZone(timeZone);
            }
        } else {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessAddress")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(registerBusiness.getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessPhone")
                            .defaultText("Business Phone cannot be empty")
                            .build());
            status = "failure";
        }

        /* When not a multi store then fetch store address. */
        if (!registerBusiness.isMultiStore()) {
            DecodedAddress decodedAddressStore = DecodedAddress.newInstance(
                    externalService.getGeocodingResults(registerBusiness.getAddressStore()),
                    registerBusiness.getAddressStore());

            if (decodedAddressStore.isNotEmpty()) {
                registerBusiness.setAddressStore(decodedAddressStore.getFormattedAddress());
                registerBusiness.setCountryShortNameStore(decodedAddressStore.getCountryShortName());

                LatLng latLng = CommonUtil.getLatLng(decodedAddressStore.getCoordinate());
                String timeZone = externalService.findTimeZone(latLng);
                registerBusiness.setTimeZoneStore(timeZone);
            }

            if (StringUtils.isBlank(registerBusiness.getDisplayName())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.displayName")
                                .defaultText("Name cannot be empty. Example: Pharmacy, Driving License, Registration")
                                .build());
                status = "failure";
            }

            if (StringUtils.isBlank(registerBusiness.getAddressStore())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.addressStore")
                                .defaultText("Stored Address cannot be empty")
                                .build());
                status = "failure";
            }

            if (StringUtils.isBlank(registerBusiness.getPhoneStoreNotFormatted())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.phoneStore")
                                .defaultText("Store Phone cannot be empty")
                                .build());
                status = "failure";
            }

            if (bizService.findStoreByPhone(registerBusiness.getPhoneStoreWithCountryCode()) != null) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.phoneStore")
                                .defaultText("Store already registered with this phone number '"
                                        + registerBusiness.getPhone()
                                        + "'. Try recovery of you account using OTP or contact customer support")
                                .build());
                status = "failure";
            }

            if (registerBusiness.getStartHourStore() == 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.startHourStore")
                                .defaultText("Store start time cannot be empty")
                                .build());
                status = "failure";
            }

            if (registerBusiness.getEndHourStore() == 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.endHourStore")
                                .defaultText("Store close time cannot be empty")
                                .build());
                status = "failure";
            }

            if (registerBusiness.getTokenAvailableFrom() == 0) {
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
