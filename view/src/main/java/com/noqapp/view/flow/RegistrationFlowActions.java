package com.noqapp.view.flow;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.flow.BusinessHour;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.utils.Formatter;
import com.noqapp.utils.ScrubbedInput;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 4:18 PM
 */
class RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationFlowActions.class);

    private ExternalService externalService;
    private BizService bizService;
    private TokenQueueService tokenQueueService;

    RegistrationFlowActions(
            ExternalService externalService,
            BizService bizService,
            TokenQueueService tokenQueueService
    ) {
        this.externalService = externalService;
        this.bizService = bizService;
        this.tokenQueueService = tokenQueueService;
    }

    @SuppressWarnings ("unused")
    public void updateProfile(Register register) {
        register.getRegisterUser().setPhone(new ScrubbedInput(register.getRegisterUser().getPhoneNotFormatted()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register register) {
        register.getRegisterBusiness().setPhone(new ScrubbedInput(Formatter.phoneCleanup(register.getRegisterBusiness().getPhone())));

        if (register.getRegisterBusiness().isMultiStore()) {
            register.getRegisterBusiness().setAddressStore(new ScrubbedInput(""));
            register.getRegisterBusiness().setPhoneStore(new ScrubbedInput(""));
            register.getRegisterBusiness().setCountryShortNameStore(new ScrubbedInput(""));
        } else {
            List<BusinessHour> businessHours = register.getRegisterBusiness().getBusinessHours();
            for (BusinessHour businessHour : businessHours) {
                if (0 == businessHour.getTokenNotAvailableFrom()) {
                    businessHour.setTokenNotAvailableFrom(businessHour.getEndHourStore());
                }
            }

            register.getRegisterBusiness().setBusinessHours(businessHours);
        }
    }

    /**
     * Add hours to store.
     * 
     * @param registerBusiness
     */
    @SuppressWarnings ("unused")
    public void fillWithBusinessHour(RegisterBusiness registerBusiness) {
        registerBusiness.setPhone(new ScrubbedInput(Formatter.phoneCleanup(registerBusiness.getPhone())));
        List<BusinessHour> businessHours = registerBusiness.getBusinessHours();
        for (BusinessHour businessHour : businessHours) {
            if (0 == businessHour.getTokenNotAvailableFrom()) {
                businessHour.setTokenNotAvailableFrom(businessHour.getEndHourStore());
            }
        }

        registerBusiness.setBusinessHours(businessHours);
    }

    private void validateAddress(BizStoreEntity bizStore) {
        if (null == bizStore.getId() || !bizStore.isValidatedUsingExternalAPI()) {
            externalService.decodeAddress(bizStore);
        }
    }

    private void validateAddress(BizNameEntity bizName) {
        if (null == bizName.getId() || !bizName.isValidatedUsingExternalAPI()) {
            externalService.decodeAddress(bizName);
        }
    }

    private void addTimezone(BizStoreEntity bizStore) {
        if (bizStore.getCoordinate() != null && bizStore.isValidatedUsingExternalAPI()) {
            externalService.updateTimezone(bizStore);
        }
    }

    /**
     * For registering additional store.
     *
     * @param registerBusiness
     * @return
     */
    RegisterBusiness registerBusinessDetails(RegisterBusiness registerBusiness) {
        try {
            BizNameEntity bizName = registerBusiness.getBusinessUser().getBizName();
            BizStoreEntity bizStore = registerStore(registerBusiness, bizName);
            tokenQueueService.create(bizStore.getCodeQR(), bizStore.getTopic(), bizStore.getDisplayName());
        } catch (Exception e) {
            LOG.error("Failed registering new bizNameId={} bizName={} reason={}",
                    registerBusiness.getBusinessUser().getBizName().getId(),
                    registerBusiness.getBusinessUser().getBizName().getBusinessName(),
                    e.getLocalizedMessage(), e);
        }
        return registerBusiness;
    }

    /**
     * For registering new business.
     * 
     * @param register
     * @return
     */
    BizNameEntity registerBusinessDetails(Register register) {
        RegisterBusiness registerBusiness = register.getRegisterBusiness();
        BizNameEntity bizName = bizService.findByPhone(registerBusiness.getPhoneWithCountryCode());

        if (null == bizName) {
            bizName = BizNameEntity.newInstance();
            bizName.setBusinessName(registerBusiness.getName());
        }
        bizName.setBusinessTypes(registerBusiness.getBusinessTypes());
        bizName.setPhone(registerBusiness.getPhoneWithCountryCode());
        bizName.setPhoneRaw(registerBusiness.getPhoneNotFormatted());
        bizName.setAddress(registerBusiness.getAddress());
        bizName.setTimeZone(registerBusiness.getTimeZone());
        bizName.setAddressOrigin(registerBusiness.getAddressOrigin());
        bizName.setMultiStore(registerBusiness.isMultiStore());
        validateAddress(bizName);

        try {
            bizService.saveName(bizName);

            /* Add a store. */
            if (!registerBusiness.isMultiStore()) {
                registerStore(registerBusiness, bizName);
            }
            return bizName;
        } catch(Exception e) {
            LOG.error("Error saving business");
            throw new RuntimeException("Error saving business");
        }
    }

    /**
     * Does store registration.
     *
     * @param registerBusiness
     * @param bizName
     * @return
     */
    private BizStoreEntity registerStore(RegisterBusiness registerBusiness, BizNameEntity bizName) {
        BizStoreEntity bizStore = bizService.findStoreByPhone(registerBusiness.getPhoneStoreWithCountryCode());
        if (null == bizStore) {
            bizStore = BizStoreEntity.newInstance();
            bizStore.setBizName(bizName);
            bizStore.setDisplayName(registerBusiness.getDisplayName());
            bizStore.setPhone(registerBusiness.getPhoneStoreWithCountryCode());
            bizStore.setPhoneRaw(registerBusiness.getPhoneStoreNotFormatted());
            bizStore.setAddress(registerBusiness.getAddressStore());
            bizStore.setTimeZone(registerBusiness.getTimeZoneStore());
            bizStore.setCodeQR(ObjectId.get().toString());
            bizStore.setAddressOrigin(registerBusiness.getAddressStoreOrigin());
            bizStore.setAllowLoggedInUser(registerBusiness.isAllowLoggedInUser());

            //TODO(hth) check if the store and business address are selected as same. Then don't call the code below.
            validateAddress(bizStore);
            try {
                bizStore.setWebLocation(registerBusiness.computeWebLocation(bizStore.getTown(), bizStore.getStateShortName()));
                bizService.saveStore(bizStore);

                String bizStoreId = bizStore.getId();
                List<StoreHourEntity> storeHours = new LinkedList<>();
                for (BusinessHour businessHour : registerBusiness.getBusinessHours()) {
                    StoreHourEntity storeHour = new StoreHourEntity(bizStoreId, businessHour.getDayOfWeek().getValue());
                    if (businessHour.isDayClosed()) {
                        storeHour.setDayClosed(businessHour.isDayClosed());
                    } else {
                        storeHour.setStartHour(businessHour.getStartHourStore());
                        storeHour.setEndHour(businessHour.getEndHourStore());
                        storeHour.setTokenAvailableFrom(businessHour.getTokenAvailableFrom());
                        storeHour.setTokenNotAvailableFrom(businessHour.getTokenNotAvailableFrom());
                    }

                    storeHours.add(storeHour);
                }

                /* Add store hours. */
                bizService.insertAll(storeHours);
                bizStore.setStoreHours(storeHours);

                /* Add timezone later as its missing id. */
                addTimezone(bizStore);
                return bizStore;
            } catch (Exception e) {
                LOG.error("Error saving store for  bizName={} bizId={}", bizName.getBusinessName(), bizName.getId());
                if (0 == bizService.getAllBizStores(bizName.getId()).size()) {
                    LOG.error("Found no store hence, starting rollback...", bizName.getBusinessName());
                    bizService.deleteBizName(bizName);
                    LOG.info("Rollback successful");
                }
                throw new RuntimeException("Error saving store");
            }
        }

        return bizStore;
    }

    boolean isBusinessUserRegistrationComplete(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        switch (businessUserRegistrationStatus) {
            case C:
                return true;
            case I:
            case N:
                return false;
            default:
                LOG.error("Reached unsupported condition={}", businessUserRegistrationStatus);
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUserRegistrationStatus);
        }
    }
}
