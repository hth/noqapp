package com.token.view.flow;

import com.token.domain.BizNameEntity;
import com.token.domain.BizStoreEntity;
import com.token.domain.flow.Register;
import com.token.domain.flow.RegisterBusiness;
import com.token.service.BizService;
import com.token.service.ExternalService;
import com.token.utils.Formatter;

/**
 * User: hitender
 * Date: 11/23/16 4:18 PM
 */
class RegistrationFlowActions {

    private ExternalService externalService;
    private BizService bizService;

    RegistrationFlowActions(ExternalService externalService, BizService bizService) {
        this.externalService = externalService;
        this.bizService = bizService;
    }

    @SuppressWarnings ("unused")
    public void updateProfile(Register register) {
        register.getRegisterUser().setPhone(Formatter.phoneCleanup(register.getRegisterUser().getPhone()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register register) {
        register.getRegisterBusiness().setPhone(Formatter.phoneCleanup(register.getRegisterBusiness().getPhone()));

        if (register.getRegisterBusiness().isMultiStore()) {
            register.getRegisterBusiness().setAddressStore(null);
            register.getRegisterBusiness().setPhoneStore(null);
            register.getRegisterBusiness().setCountryShortNameStore(null);
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
        BizNameEntity bizName = bizService.findMatchingBusiness(
                registerBusiness.getName(),
                registerBusiness.getPhoneNotFormatted());

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
        bizService.saveName(bizName);

        /* Add a store. */
        if (!registerBusiness.isMultiStore()) {

            BizStoreEntity bizStore = bizService.findMatchingStore(
                    registerBusiness.getAddress(),
                    registerBusiness.getPhoneNotFormatted());
            if (bizStore == null) {
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

                //TODO(hth) check if the store and business address are selected as same. Then don't call the code below.
                validateAddress(bizStore);
                bizService.saveStore(bizStore);
                /* Since missing id. */
                addTimezone(bizStore);
            }
        }
        return bizName;
    }
}
