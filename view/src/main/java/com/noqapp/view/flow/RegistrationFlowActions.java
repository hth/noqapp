package com.noqapp.view.flow;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.service.BizService;
import com.noqapp.service.ExternalService;
import com.noqapp.utils.Formatter;

/**
 * User: hitender
 * Date: 11/23/16 4:18 PM
 */
class RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationFlowActions.class);

    private ExternalService externalService;
    private BizService bizService;

    RegistrationFlowActions(ExternalService externalService, BizService bizService) {
        this.externalService = externalService;
        this.bizService = bizService;
    }

    @SuppressWarnings ("unused")
    public void updateProfile(Register register) {
        register.getRegisterUser().setPhone(register.getRegisterUser().getPhoneNotFormatted());
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register register) {
        register.getRegisterBusiness().setPhone(Formatter.phoneCleanup(register.getRegisterBusiness().getPhone()));

        if (register.getRegisterBusiness().isMultiStore()) {
            register.getRegisterBusiness().setAddressStore(null);
            register.getRegisterBusiness().setPhoneStore(null);
            register.getRegisterBusiness().setCountryShortNameStore(null);
        } else {
            if (0 == register.getRegisterBusiness().getTokenNotAvailableFrom()) {
                register.getRegisterBusiness().setTokenNotAvailableFrom(register.getRegisterBusiness().getEndHourStore());
            }
        }
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
        bizName.setMultiStore(registerBusiness.isMultiStore());
        validateAddress(bizName);

        try {
            bizService.saveName(bizName);

            /* Add a store. */
            if (!registerBusiness.isMultiStore()) {

                BizStoreEntity bizStore = bizService.findStoreByPhone(registerBusiness.getPhoneStoreWithCountryCode());
                if (null == bizStore) {
                    bizStore = BizStoreEntity.newInstance();
                    bizStore.setBizName(bizName);
                    bizStore.setDisplayName(registerBusiness.getDisplayName());
                    bizStore.setPhone(registerBusiness.getPhoneStoreWithCountryCode());
                    bizStore.setPhoneRaw(registerBusiness.getPhoneStoreNotFormatted());
                    bizStore.setAddress(registerBusiness.getAddress());
                    bizStore.setTimeZone(registerBusiness.getTimeZone());
                    bizStore.setStartHour(registerBusiness.getStartHourStore());
                    bizStore.setEndHour(registerBusiness.getEndHourStore());
                    bizStore.setTokenAvailableFrom(registerBusiness.getTokenAvailableFrom());
                    bizStore.setTokenNotAvailableFrom(registerBusiness.getTokenNotAvailableFrom());
                    bizStore.setCodeQR(ObjectId.get().toString());

                    //TODO(hth) check if the store and business address are selected as same. Then don't call the code below.
                    validateAddress(bizStore);
                    try {
                        bizService.saveStore(bizStore);
                        /* Add timezone later as its missing id. */
                        addTimezone(bizStore);
                    } catch (Exception e) {
                        LOG.error("Error saving store name={}, starting rollback...", bizName.getBusinessName());
                        bizService.deleteBizName(bizName);
                        LOG.info("Rollback successful");
                        throw new RuntimeException("Error saving store");
                    }
                }
            }
            return bizName;
        } catch(Exception e) {
            LOG.error("Error saving business");
            throw new RuntimeException("Error saving business");
        }
    }
}
