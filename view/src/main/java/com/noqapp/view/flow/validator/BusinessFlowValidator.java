package com.noqapp.view.flow.validator;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.flow.BusinessHour;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.service.BizService;
import com.noqapp.service.ExternalService;
import com.noqapp.utils.CommonUtil;

import java.util.List;
import java.util.Set;

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
                            .source("registerBusiness.name")
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
                            .source("registerBusiness.address")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(registerBusiness.getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.phone")
                            .defaultText("Business Phone cannot be empty")
                            .build());
            status = "failure";
        }

        if (status.equalsIgnoreCase("success")) {
            Set<BizStoreEntity> bizStores = bizService.bizSearch(
                    registerBusiness.getName(),
                    registerBusiness.getAddress(),
                    registerBusiness.getPhoneWithCountryCode());

            if (bizStores.size() != 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.name")
                                .defaultText(
                                        "Business Name already exist wth this name or address or phone. " +
                                                "Please email us at contact@noqapp.com.")
                                .build());
                status = "failure";
            }

        }

        /* When not a multi store then fetch store address. */
        if (!registerBusiness.isMultiStore()) {
            if (StringUtils.isBlank(registerBusiness.getAddressStore())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.addressStore")
                                .defaultText("Store Address cannot be empty")
                                .build());
                status = "failure";
            } else {
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
            }

            if (StringUtils.isBlank(registerBusiness.getDisplayName())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.displayName")
                                .defaultText("Queue Name cannot be empty. " +
                                        "Queue Names can be like: Pharmacy, Driving License, Dinner Registration")
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
            } else {
                if (bizService.findStoreByPhone(registerBusiness.getPhoneStoreWithCountryCode()) != null) {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source("registerBusiness.phoneStore")
                                    .defaultText("Store already registered with this phone number '"
                                            + registerBusiness.getPhone()
                                            + "'. Please email us at contact@noqapp.com.")
                                    .build());
                    status = "failure";
                }
            }
        }

        LOG.info("Validate business rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }

    /**
     * Validate business hours.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateBusinessHours(Register register, MessageContext messageContext) {
        LOG.info("Validate business rid={}", register.getRegisterUser().getRid());
        String status = "success";

        final RegisterBusiness registerBusiness = register.getRegisterBusiness();
        List<BusinessHour> businessHours = registerBusiness.getBusinessHours();

        for (BusinessHour businessHour : businessHours) {
            if (businessHour.getStartHourStore() == 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.startHourStore")
                                .defaultText("Store Start Time cannot be empty")
                                .build());
                status = "failure";
            }

            if (businessHour.getEndHourStore() == 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.endHourStore")
                                .defaultText("Store Close Time cannot be empty")
                                .build());
                status = "failure";
            }

            if (businessHour.getTokenAvailableFrom() == 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerBusiness.tokenAvailableFrom")
                                .defaultText("Token Available Time cannot be empty. This is the time from when Token would be available to users.")
                                .build());
                status = "failure";
            }
        }

        LOG.info("Validate business rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }
}
