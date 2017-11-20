package com.noqapp.view.flow.merchant.validator;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

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
import com.noqapp.domain.shared.Geocode;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.ExternalService;
import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.view.controller.access.LandingController;

import java.util.List;
import java.util.Map;
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
     * Validate business user profile and business.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateBusinessDetails(Register register, MessageContext messageContext) {
        LOG.info("Validate business qid={}", register.getRegisterUser().getQueueUserId());
        String status = LandingController.SUCCESS;

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
        
        if (StringUtils.isBlank(registerBusiness.getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.address")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        } else {
            DecodedAddress decodedAddress;
            if (registerBusiness.isSelectFoundAddress()) {
                decodedAddress = registerBusiness.getFoundAddresses().get(registerBusiness.getFoundAddressPlaceId());
                registerBusiness.setAddress(new ScrubbedInput(decodedAddress.getFormattedAddress()));
                registerBusiness.setAddressOrigin(AddressOriginEnum.G);
            } else if(registerBusiness.getFoundAddresses().isEmpty()) {
                Geocode geocode = Geocode.newInstance(
                        externalService.getGeocodingResults(registerBusiness.getAddress()),
                        registerBusiness.getAddress());
                registerBusiness.setFoundAddresses(geocode.getFoundAddresses());
                decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);

                if (decodedAddress.isNotBlank()) {
                    if (geocode.getResults().length > 1 || geocode.isAddressMisMatch()) {
                        messageContext.addMessage(
                                new MessageBuilder()
                                        .error()
                                        .source("registerBusiness.address")
                                        .defaultText("Found other matching address(es). Please select 'Best Matching Business Address' or if you choose 'Business Address' then click Next.")
                                        .build());
                        status = "failure";
                    }
                } else {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source("registerBusiness.address")
                                    .defaultText("Failed decoding your address. Please contact support if this error persists.")
                                    .build());
                    status = "failure";
                }
            } else {
                /*
                 * Since user has choose to select the address entered by user, we take what we found and
                 * replace the other parameter with decoded address and keep the original address same.
                 */
                Map.Entry<String, DecodedAddress> entry = registerBusiness.getFoundAddresses().entrySet().iterator().next();
                decodedAddress = entry.getValue();
                registerBusiness.setAddressOrigin(AddressOriginEnum.S);
            }
            
            if (decodedAddress.isNotBlank()) {
                registerBusiness.setCountryShortName(new ScrubbedInput(decodedAddress.getCountryShortName()));

                LatLng latLng = CommonUtil.getLatLng(decodedAddress.getCoordinate());
                String timeZone = externalService.findTimeZone(latLng);
                registerBusiness.setTimeZone(new ScrubbedInput(timeZone));
            }
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
            /* Ignore finding business when editing. */
            if (StringUtils.isBlank(registerBusiness.getBizId())) {
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
        }

        /* When not a multi store then fetch store address. */
        if (!registerBusiness.isMultiStore()) {
            status = validateStoreDetails(registerBusiness, "registerBusiness.", messageContext);
        } else {
            if (status.equalsIgnoreCase(LandingController.SUCCESS)) {
                /* When selected multiStore then jump to review on success. */
                LOG.info("Skipped store hours as selected multiStore={}", registerBusiness.isMultiStore());
                status = "skipStoreHours";
            }
        }

        LOG.info("Validate business qid={} status={}", register.getRegisterUser().getQueueUserId(), status);
        return status;
    }

    /**
     * Validate store.
     *
     * @param registerBusiness
     * @param source            adaptable source based on where this validation is being called from
     * @param messageContext
     * @return
     */
    public String validateStoreDetails(RegisterBusiness registerBusiness, String source, MessageContext messageContext) {
        String status = LandingController.SUCCESS;
        if (StringUtils.isBlank(registerBusiness.getAddressStore())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source(source + "addressStore")
                            .defaultText("Store Address cannot be empty")
                            .build());
            status = "failure";
        } else {
            DecodedAddress decodedAddressStore;
            if (registerBusiness.isSelectFoundAddressStore()) {
                decodedAddressStore = registerBusiness.getFoundAddressStores().get(registerBusiness.getFoundAddressStorePlaceId());
                registerBusiness.setAddressStore(new ScrubbedInput(decodedAddressStore.getFormattedAddress()));
                registerBusiness.setAddressStoreOrigin(AddressOriginEnum.G);
            } else if(registerBusiness.getFoundAddressStores().isEmpty()) {
                Geocode geocode = Geocode.newInstance(
                        externalService.getGeocodingResults(registerBusiness.getAddressStore()),
                        registerBusiness.getAddressStore());
                registerBusiness.setFoundAddressStores(geocode.getFoundAddresses());
                decodedAddressStore = DecodedAddress.newInstance(geocode.getResults(), 0);

                if (decodedAddressStore.isNotBlank()) {
                    if (geocode.getResults().length > 1 || geocode.isAddressMisMatch()) {
                        messageContext.addMessage(
                                new MessageBuilder()
                                        .error()
                                        .source(source + "addressStore")
                                        .defaultText("Found other matching address(es). Please select 'Best Matching Store Address' or if you choose 'Store Address' then click Next.")
                                        .build());
                        status = "failure";
                    }
                } else {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source(source + "addressStore")
                                    .defaultText("Failed decoding your address. Please contact support if this error persists.")
                                    .build());
                    status = "failure";
                }
            } else {
                /*
                 * Since user has choose to select the address entered by user, we take what we found and
                 * replace the other parameter with decoded address and keep the original address same.
                 */
                Map.Entry<String, DecodedAddress> entry = registerBusiness.getFoundAddressStores().entrySet().iterator().next();
                decodedAddressStore = entry.getValue();
                registerBusiness.setAddressStoreOrigin(AddressOriginEnum.S);
            }

            if (decodedAddressStore.isNotBlank()) {
                registerBusiness.setCountryShortNameStore(new ScrubbedInput(decodedAddressStore.getCountryShortName()));

                LatLng latLng = CommonUtil.getLatLng(decodedAddressStore.getCoordinate());
                String timeZone = externalService.findTimeZone(latLng);
                registerBusiness.setTimeZoneStore(new ScrubbedInput(timeZone));
            }
        }

        if (StringUtils.isBlank(registerBusiness.getDisplayName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source(source + "displayName")
                            .defaultText("Queue Name cannot be empty. " +
                                    "Queue Names can be like: Pharmacy, Driving License, Dinner Registration")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(registerBusiness.getPhoneStoreNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source(source + "phoneStore")
                            .defaultText("Store Phone cannot be empty")
                            .build());
            status = "failure";
        }
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
        LOG.info("Validate business qid={}", register.getRegisterUser().getQueueUserId());
        final RegisterBusiness registerBusiness = register.getRegisterBusiness();
        return validateBusinessHours(registerBusiness, "registerBusiness.", messageContext);
    }


    /**
     * Validate store hours.
     *
     * @param registerBusiness
     * @param source            adaptable source based on where this validation is being called from
     * @param messageContext
     * @return
     */
    public String validateBusinessHours(RegisterBusiness registerBusiness, String source, MessageContext messageContext) {
        String status = LandingController.SUCCESS;
        List<BusinessHour> businessHours = registerBusiness.getBusinessHours();

        for (BusinessHour businessHour : businessHours) {
            if (!businessHour.isDayClosed()) {
                if (businessHour.getStartHourStore() == 0) {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].startHourStore")
                                    .defaultText("Specify Store Start Time for "
                                            + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name()))
                                    .build());
                    status = "failure";
                }

                if (businessHour.getEndHourStore() == 0) {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].endHourStore")
                                    .defaultText("Specify Store Close Time for "
                                            + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name()))
                                    .build());
                    status = "failure";
                }

                if (businessHour.getTokenAvailableFrom() == 0) {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].tokenAvailableFrom")
                                    .defaultText("Specify Token Available Time for "
                                            + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                            + ". This is the time from when Token would be available to users.")
                                    .build());
                    status = "failure";
                }
            }
        }

        LOG.info("Validate business qid={} status={}", registerBusiness.getBusinessUser().getQueueUserId(), status);
        return status;
    }
}
