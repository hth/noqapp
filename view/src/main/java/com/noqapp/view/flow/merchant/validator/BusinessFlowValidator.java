package com.noqapp.view.flow.merchant.validator;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.flow.BusinessHour;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.shared.Geocode;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.ExternalService;
import com.noqapp.view.controller.access.LandingController;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validating business related information.
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
     * @param mode           create or edit are the modes defined in flow xml
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validateBusinessDetails(Register register, String mode, MessageContext messageContext) {
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

        if (null == registerBusiness.getBusinessType() && mode.equalsIgnoreCase("create")) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("registerBusiness.businessType")
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

                if (StringUtils.isBlank(registerBusiness.getArea())) {
                    registerBusiness.setArea(new ScrubbedInput(decodedAddress.getArea()));
                }

                if (StringUtils.isBlank(registerBusiness.getTown())) {
                    registerBusiness.setTown(new ScrubbedInput(decodedAddress.getTown()));
                }
            } else if (registerBusiness.getFoundAddresses().isEmpty()) {
                Geocode geocode = Geocode.newInstance(
                    externalService.getGeocodingResults(registerBusiness.getAddress()),
                    registerBusiness.getAddress());
                registerBusiness.setFoundAddresses(geocode.getFoundAddresses());
                decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);

                if (StringUtils.isBlank(registerBusiness.getArea())) {
                    registerBusiness.setArea(new ScrubbedInput(decodedAddress.getArea()));
                }

                if (StringUtils.isBlank(registerBusiness.getTown())) {
                    registerBusiness.setTown(new ScrubbedInput(decodedAddress.getTown()));
                }

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

                if (StringUtils.isBlank(registerBusiness.getArea())) {
                    registerBusiness.setArea(new ScrubbedInput(decodedAddress.getArea()));
                }

                if (StringUtils.isBlank(registerBusiness.getTown())) {
                    registerBusiness.setTown(new ScrubbedInput(decodedAddress.getTown()));
                }
            }

            if (decodedAddress.isNotBlank()) {
                registerBusiness.setCountryShortName(new ScrubbedInput(decodedAddress.getCountryShortName()));

                LatLng latLng = CommonUtil.getLatLng(decodedAddress.getCoordinate());
                String timeZone = externalService.findTimeZone(latLng);
                registerBusiness.setTimeZone(new ScrubbedInput(timeZone));
            }

            if (StringUtils.isBlank(registerBusiness.getArea())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("registerBusiness.area")
                        .defaultText("Business Town cannot be empty")
                        .build());
                status = "failure";
            }

            if (StringUtils.isBlank(registerBusiness.getTown())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("registerBusiness.town")
                        .defaultText("Business Area cannot be empty")
                        .build());
                status = "failure";
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
        } else if (mode.equalsIgnoreCase("create")) {
            LOG.info("Checking if business exists with phone={}", registerBusiness.getPhoneWithCountryCode());
            BizNameEntity bizName = bizService.findByPhone(registerBusiness.getPhoneWithCountryCode());
            if (null != bizName && !bizName.getId().equalsIgnoreCase(registerBusiness.getBizId())) {
                LOG.warn("Business exists with phone={} existing bizName={}", registerBusiness.getPhoneWithCountryCode(), bizName.getBusinessName());
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("registerBusiness.phone")
                        .defaultText("Business Phone already exists. Contact support for help.")
                        .build());
                status = "failure";
            }
        }

        if (mode.equalsIgnoreCase("edit") && registerBusiness.getBusinessType() == null) {
            BizNameEntity bizName = bizService.getByBizNameId(registerBusiness.getBizId());
            registerBusiness.setBusinessType(bizName.getBusinessType());
        }

        if (status.equalsIgnoreCase("success") && mode.equalsIgnoreCase("create")) {
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

        if (status.equalsIgnoreCase(LandingController.SUCCESS)) {
            switch (register.getRegisterBusiness().getBusinessType()) {
                case DO:
                    register.getRegisterBusiness().setPopulateAmenitiesAndFacilities(true);
                    break;
                case RS:
                case RSQ:
                case FT:
                case FTQ:
                case BA:
                case BAQ:
                case ST:
                case STQ:
                case GS:
                case GSQ:
                case CF:
                case CFQ:
                    LOG.info("Skipped amenities and facilities for businessType={}", register.getRegisterBusiness().getBusinessType());
                    status = status + ".SKIP_AMENITIES_FACILITIES";
            }
        }

        LOG.info("Validate business qid={} status={}", register.getRegisterUser().getQueueUserId(), status);
        return status;
    }

    /**
     * Validate store.
     *
     * @param registerBusiness
     * @param source           adaptable source based on where this validation is being called from
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validateStoreDetails(RegisterBusiness registerBusiness, String source, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (registerBusiness.isBusinessAddressAsStore()) {
            registerBusiness.setAddressStore(new ScrubbedInput(registerBusiness.getBusinessUser().getBizName().getAddress()));
            registerBusiness.setPhoneStore(new ScrubbedInput(registerBusiness.getBusinessUser().getBizName().getPhoneRaw()));
        }

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

                if (StringUtils.isBlank(registerBusiness.getAreaStore())) {
                    registerBusiness.setAreaStore(new ScrubbedInput(decodedAddressStore.getArea()));
                }

                if (StringUtils.isBlank(registerBusiness.getTownStore())) {
                    registerBusiness.setTownStore(new ScrubbedInput(decodedAddressStore.getTown()));
                }
            } else if (registerBusiness.getFoundAddressStores().isEmpty()) {
                Geocode geocode = Geocode.newInstance(
                    externalService.getGeocodingResults(registerBusiness.getAddressStore()),
                    registerBusiness.getAddressStore());
                registerBusiness.setFoundAddressStores(geocode.getFoundAddresses());
                decodedAddressStore = DecodedAddress.newInstance(geocode.getResults(), 0);

                if (StringUtils.isBlank(registerBusiness.getAreaStore())) {
                    registerBusiness.setAreaStore(new ScrubbedInput(decodedAddressStore.getArea()));
                }

                if (StringUtils.isBlank(registerBusiness.getTownStore())) {
                    registerBusiness.setTownStore(new ScrubbedInput(decodedAddressStore.getTown()));
                }

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

                if (StringUtils.isBlank(registerBusiness.getAreaStore())) {
                    registerBusiness.setAreaStore(new ScrubbedInput(decodedAddressStore.getArea()));
                }

                if (StringUtils.isBlank(registerBusiness.getTownStore())) {
                    registerBusiness.setTownStore(new ScrubbedInput(decodedAddressStore.getTown()));
                }
            }

            if (decodedAddressStore.isNotBlank()) {
                registerBusiness.setCountryShortNameStore(new ScrubbedInput(decodedAddressStore.getCountryShortName()));

                LatLng latLng = CommonUtil.getLatLng(decodedAddressStore.getCoordinate());
                String timeZone = externalService.findTimeZone(latLng);
                registerBusiness.setTimeZoneStore(new ScrubbedInput(timeZone));
            }

            if (StringUtils.isBlank(registerBusiness.getAreaStore())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "areaStore")
                        .defaultText("Store Town cannot be empty")
                        .build());
                status = "failure";
            }

            if (StringUtils.isBlank(registerBusiness.getTownStore())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "townStore")
                        .defaultText("Store City cannot be empty")
                        .build());
                status = "failure";
            }
        }

        if (registerBusiness.isBusinessAddressAsStore()) {
            /* Since this is overwritten above when address is fetched. */
            //TODO(hth) should skip fetching from Google when address already exists.
            registerBusiness.setAddressStoreOrigin(registerBusiness.getBusinessUser().getBizName().getAddressOrigin());
        }

        if (StringUtils.isBlank(registerBusiness.getDisplayName())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "displayName")
                    .defaultText("Online " + registerBusiness.getBusinessType().getClassifierTitle() + " Name cannot be empty. " +
                        registerBusiness.getBusinessType().getClassifierTitle() + " Names can be like: Pharmacy, Driving License, Dinner Reservation")
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

        boolean foundInStoreBusinessSelection = false;
        if (null != registerBusiness.getBusinessType()) {
            if (registerBusiness.getBusinessType() == registerBusiness.getStoreBusinessType()) {
                foundInStoreBusinessSelection = true;
            }
        }

        if (!foundInStoreBusinessSelection) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "storeBusinessType")
                    .defaultText("'Queue for' has to be a subset of 'Business Type'")
                    .build());
            status = "failure";
        }

        /* Checks if it has self defined business category or system defined. DO, BK and HS has system defined. */
        switch (registerBusiness.getStoreBusinessType()) {
            case DO:
            case BK:
            case HS:
            case CD:
            case CDQ:
            case LB:
                break;
            default:
                if (StringUtils.isNotBlank(registerBusiness.getBizCategoryId())) {
                    if (!Validate.isValidObjectId(registerBusiness.getBizCategoryId())) {
                        LOG.error("BizCategoryId should be ObjectId but its {}", registerBusiness.getBizCategoryId());
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source(source + "bizCategoryId")
                                .defaultText("Internal error on Category. Please contact support.")
                                .build());
                        status = "failure";
                    }
                }
        }

        return status;
    }

    /**
     * Validate queue settings.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validateQueueSettings(Register register, MessageContext messageContext) {
        LOG.info("Validate queue settings qid={}", register.getRegisterUser().getQueueUserId());
        final RegisterBusiness registerBusiness = register.getRegisterBusiness();
        return validateQueueSettings(registerBusiness, "registerBusiness.", messageContext);
    }

    /**
     * Validate business hours.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings("unused")
    public String validateBusinessHours(Register register, MessageContext messageContext) {
        LOG.info("Validate business qid={}", register.getRegisterUser().getQueueUserId());
        final RegisterBusiness registerBusiness = register.getRegisterBusiness();
        return validateBusinessHours(registerBusiness, "registerBusiness.", messageContext);
    }

    @SuppressWarnings("unused")
    public String validateAmenitiesAndFacilitiesSettings(RegisterBusiness registerBusiness, String source, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        switch (registerBusiness.getBusinessType()) {
            case RS:
            case BA:
            case ST:
            case GS:
            case CF:
            case CD:
            case FT:
                if (registerBusiness.getAcceptedDeliveries() == null || registerBusiness.getAcceptedDeliveries().size() == 0) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "acceptedDeliveries")
                            .defaultText("Select at least one option under Supported Delivery")
                            .build());
                    status = "failure";
                }

                if (registerBusiness.getAcceptedPayments() == null || registerBusiness.getAcceptedPayments().size() == 0) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "acceptedPayments")
                            .defaultText("Select at least one option under Accepted Payment")
                            .build());
                    status = "failure";
                }
                break;
            case HS:
            case DO:
            default:
                //Do nothing
        }

        return status;
    }

    public String validateQueueSettings(RegisterBusiness registerBusiness, String source, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (!StringUtils.isNumeric(String.valueOf(registerBusiness.getAvailableTokenCount()))) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "availableTokenCount")
                    .defaultText("Limited token is not valid. Should be a positive number.")
                    .build());
            status = "failure";
        }

        if (registerBusiness.getWalkInState() == null) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source(source + "walkInState")
                    .defaultText("Walk-in state cannot be empty")
                    .build());
            status = "failure";
        }

        /* When business accepts orders. */
        if (MessageOriginEnum.O == registerBusiness.getBusinessType().getMessageOrigin()) {
            if (registerBusiness.getDeliveryRange() < 3) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "deliveryRange")
                        .defaultText("Delivery Radius cannot be less than 3kms")
                        .build());
                status = "failure";
            }
        }
        return status;
    }

    /**
     * Validate store hours.
     *
     * @param registerBusiness
     * @param source           adaptable source based on where this validation is being called from
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
                            .defaultText("Specify " + registerBusiness.getLabelForOrderOrToken() + " Available Time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + ". This is the time from when Token would be available to users.")
                            .build());
                    status = "failure";
                }

                if (businessHour.getStartHourStore() > 2359) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].startHourStore")
                            .defaultText("Store Start Time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot exceed 2359")
                            .build());
                    status = "failure";
                }

                if (businessHour.getEndHourStore() > 2359) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].endHourStore")
                            .defaultText("Store Close Time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot exceed 2359")
                            .build());
                    status = "failure";
                }

                if (businessHour.getTokenAvailableFrom() > 2359) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].tokenAvailableFrom")
                            .defaultText(registerBusiness.getLabelForOrderOrToken() + " Available Time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot exceed 2359")
                            .build());
                    status = "failure";
                }

                if (businessHour.getTokenNotAvailableFrom() > 2359) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].tokenNotAvailableFrom")
                            .defaultText(registerBusiness.getLabelForOrderOrToken() + " Not Available After for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot exceed 2359")
                            .build());
                    status = "failure";
                }

                if (businessHour.getLunchTimeStart() > 2359) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeStart")
                            .defaultText("Lunch Start Time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot exceed 2359")
                            .build());
                    status = "failure";
                }

                if (businessHour.getLunchTimeEnd() > 2359) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeEnd")
                            .defaultText("Lunch End Time for  "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot exceed 2359")
                            .build());
                    status = "failure";
                }

                if (businessHour.getLunchTimeStart() != 0 && businessHour.getLunchTimeStart() < businessHour.getStartHourStore()) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeStart")
                            .defaultText("Lunch Start Time for  "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " should be after store start time. Or set value to 0.")
                            .build());
                    status = "failure";
                }

                if (businessHour.getLunchTimeEnd() != 0 && (businessHour.getLunchTimeEnd() > businessHour.getEndHourStore() || businessHour.getLunchTimeEnd() < businessHour.getStartHourStore())) {
                    if (businessHour.getLunchTimeEnd() > businessHour.getEndHourStore()) {
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeEnd")
                                .defaultText("Lunch End Time for  "
                                    + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                    + " should be before store close time.")
                                .build());
                        status = "failure";
                    }

                    if (businessHour.getLunchTimeEnd() < businessHour.getStartHourStore()) {
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeEnd")
                                .defaultText("Lunch End Time for  "
                                    + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                    + " should be after store start time.")
                                .build());
                        status = "failure";
                    }
                }

                if (businessHour.getLunchTimeStart() > 0) {
                    if (businessHour.getLunchTimeEnd() == 0) {
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeEnd")
                                .defaultText("Lunch End Time for  "
                                    + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                    + " should be set or remove Lunch Start Time")
                                .build());
                        status = "failure";
                    }
                }

                if (businessHour.getLunchTimeEnd() > 0) {
                    if (businessHour.getLunchTimeStart() == 0) {
                        messageContext.addMessage(
                            new MessageBuilder()
                                .error()
                                .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeStart")
                                .defaultText("Lunch Start Time for  "
                                    + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                    + " should be set or remove Lunch End Time.")
                                .build());
                        status = "failure";
                    }
                }

                if (businessHour.getLunchTimeStart() == businessHour.getStartHourStore() || businessHour.getLunchTimeEnd() == businessHour.getEndHourStore()) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeStart")
                            .defaultText("Lunch Start Time or End Time for  "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " should not match " + registerBusiness.getStoreBusinessType().getClassifierTitle() + " Start Time or Close Time.")
                            .build());
                    status = "failure";
                }

                if (businessHour.getLunchTimeStart() > businessHour.getLunchTimeEnd()) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].lunchTimeStart")
                            .defaultText("Lunch Start Time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " should be before Lunch End Time.")
                            .build());
                    status = "failure";
                }

                if (businessHour.getEndHourStore() < businessHour.getStartHourStore()) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].endHourStore")
                            .defaultText("Store Close Time has for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name()) +
                                " to be after Store Start Time.")
                            .build());
                    status = "failure";
                }

                if (businessHour.getEndHourStore() < businessHour.getTokenNotAvailableFrom()) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].tokenNotAvailableFrom")
                            .defaultText(registerBusiness.getLabelForOrderOrToken() + " Not Available After for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " has to be before Store Close Time.")
                            .build());
                    status = "failure";
                }
            }
        }

        LOG.info("Validate business qid={} status={}", registerBusiness.getBusinessUser().getQueueUserId(), status);
        if (status.equalsIgnoreCase(LandingController.SUCCESS)) {
            switch (registerBusiness.getBusinessType().getBusinessSupport()) {
                case OD:
                    status = registerBusiness.getBusinessType().getMessageOrigin() + status;

                    /* There are no appointments for orders. */
                    registerBusiness.setAppointmentState(AppointmentStateEnum.O);
                    break;
                case QQ:
                case OQ:
                    status = registerBusiness.getBusinessType().getMessageOrigin() + status;
                    break;
                default:
                    LOG.error("Reached unreachable condition");
                    throw new UnsupportedOperationException("Reached Unsupported Condition");
            }
        }
        registerBusiness.setAppointmentStates(AppointmentStateEnum.appointmentsFor(registerBusiness.getBusinessType().getBusinessSupport()));
        return status;
    }

    @SuppressWarnings("unused")
    public String validateAppointmentSettings(RegisterBusiness registerBusiness, String source, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (registerBusiness.getAppointmentState() != AppointmentStateEnum.O) {
            if (registerBusiness.getAppointmentDuration() < 1) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "appointment duration")
                        .defaultText("Appointment duration cannot be less than 1 minute")
                        .build());
                status = "failure";
            }

            if (registerBusiness.getAppointmentDuration() > 60) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "appointment duration")
                        .defaultText("Appointment duration cannot be greater than 60 minutes")
                        .build());
                status = "failure";
            }

            if (registerBusiness.getAppointmentOpenHowFar() > 52) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "appointment windows")
                        .defaultText("Appointment window cannot exceed more than 52 weeks")
                        .build());
                status = "failure";
            }

            if (registerBusiness.getAppointmentOpenHowFar() < 1) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source(source + "appointment windows")
                        .defaultText("Appointment window has to be at least 1 week")
                        .build());
                status = "failure";
            }

        }
        return status;
    }

    @SuppressWarnings("unused")
    public String validateAppointmentHours(RegisterBusiness registerBusiness, String source, MessageContext messageContext) {
        String status = LandingController.SUCCESS;
        List<BusinessHour> businessHours = registerBusiness.getBusinessHours();

        for (BusinessHour businessHour : businessHours) {
            if (!businessHour.isDayClosed()) {
                if (businessHour.getAppointmentStartHour() == 0) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].appointmentStartHour")
                            .defaultText("Specify Appointment Available Time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + ". This is the time from when users would be able to book appointment.")
                            .build());
                    status = "failure";
                }

                if (businessHour.getAppointmentEndHour() == 0) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].appointmentEndHour")
                            .defaultText("Specify Appointment End Time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + ". This is the time after which appointment booking would not be available.")
                            .build());
                    status = "failure";
                }

                if (businessHour.getAppointmentStartHour() > 2359) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].appointmentStartHour")
                            .defaultText("Appointment start time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot exceed 2359")
                            .build());
                    status = "failure";
                }

                if (businessHour.getAppointmentEndHour() > 2359) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].appointmentEndHour")
                            .defaultText("Appointment end time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot exceed 2359")
                            .build());
                    status = "failure";
                }

                if (businessHour.getAppointmentStartHour() < businessHour.getStartHourStore()) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].appointmentStartHour")
                            .defaultText("Appointment start time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot start before store start hour")
                            .build());
                    status = "failure";
                }

                if (businessHour.getAppointmentEndHour() > businessHour.getEndHourStore()) {
                    messageContext.addMessage(
                        new MessageBuilder()
                            .error()
                            .source(source + "businessHours[" + businessHour.getDayOfWeek().ordinal() + "].appointmentEndHour")
                            .defaultText("Appointment end time for "
                                + WordUtils.capitalizeFully(businessHour.getDayOfWeek().name())
                                + " cannot end after store end hour")
                            .build());
                    status = "failure";
                }
            }
        }

        LOG.info("Validate business qid={} status={}", registerBusiness.getBusinessUser().getQueueUserId(), status);
        return status;
    }

    @SuppressWarnings("unused")
    public String validateBusinessProperties(RegisterBusiness registerBusiness, MessageContext messageContext) {
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(registerBusiness.getLimitServiceByDays())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("registerBusiness.limitServiceByDays")
                    .defaultText("Limit Service By Days cannot be empty.")
                    .build());
            status = "failure";
        }

        try {
            Integer.parseInt(registerBusiness.getLimitServiceByDays());
            if (!StringUtils.isNumeric(registerBusiness.getLimitServiceByDays())) {
                throw new NumberFormatException("Number is not number");
            }
        } catch (NumberFormatException e) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("registerBusiness.limitServiceByDays")
                    .defaultText("Specify Limit Service By Days as a whole positive number.")
                    .build());
            status = "failure";
        }

        return status;
    }
}
