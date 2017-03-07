package com.token.view.flow;

import com.token.domain.BizNameEntity;
import com.token.domain.BizStoreEntity;
import com.token.domain.flow.Register;
import com.token.domain.shared.DecodedAddress;
import com.token.service.BizService;
import com.token.service.ExternalService;
import com.token.utils.CommonUtil;

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
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(register.getRegisterUser().getAddress()), register.getRegisterUser().getAddress());
        if (decodedAddress.isNotEmpty()) {
            register.getRegisterUser().setAddress(decodedAddress.getFormattedAddress());
            register.getRegisterUser().setCountryShortName(decodedAddress.getCountryShortName());
        }
        register.getRegisterUser().setPhone(CommonUtil.phoneCleanup(register.getRegisterUser().getPhone()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register register) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(register.getRegisterBusiness().getAddress()), register.getRegisterBusiness().getAddress());
        if (decodedAddress.isNotEmpty()) {
            register.getRegisterBusiness().setAddress(decodedAddress.getFormattedAddress());
            register.getRegisterBusiness().setCountryShortName(decodedAddress.getCountryShortName());
        }
        register.getRegisterBusiness().setPhone(CommonUtil.phoneCleanup(register.getRegisterBusiness().getPhone()));

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

    BizNameEntity registerBusinessDetails(Register register) {
        BizNameEntity bizName = bizService.findMatchingBusiness(register.getRegisterBusiness().getName());
        if (null == bizName) {
            bizName = BizNameEntity.newInstance();
            bizName.setBusinessName(register.getRegisterBusiness().getName());
        }
        bizName.setBusinessTypes(register.getRegisterBusiness().getBusinessTypes());
        bizName.setPhone(register.getRegisterBusiness().getPhone());
        bizName.setAddress(register.getRegisterBusiness().getAddress());
        bizName.setMultiStore(register.getRegisterBusiness().isMultiStore());
        validateAddress(bizName);
        bizService.saveName(bizName);

        /* Add a store. */
        if (!register.getRegisterBusiness().isMultiStore()) {

            BizStoreEntity bizStore = bizService.findMatchingStore(
                    register.getRegisterBusiness().getAddress(),
                    register.getRegisterBusiness().getBusinessPhoneNotFormatted());
            if (bizStore == null) {
                bizStore = BizStoreEntity.newInstance();
                bizStore.setBizName(bizName);
                bizStore.setDisplayName(register.getRegisterBusiness().getDisplayName());
                bizStore.setPhone(register.getRegisterBusiness().getPhone());
                bizStore.setAddress(register.getRegisterBusiness().getAddress());
                bizStore.setStartHour(register.getRegisterBusiness().getStartHourStore());
                bizStore.setEndHour(register.getRegisterBusiness().getEndHourStore());
                bizStore.setTokenAvailableFrom(register.getRegisterBusiness().getTokenAvailableFrom());
                bizStore.setTokenNotAvailableFrom(register.getRegisterBusiness().getTokenNotAvailableFrom());

                //TODO(hth) check if the store and business address are selected as same. Then don't call the code below.
                validateAddress(bizStore);
                bizService.saveStore(bizStore);
            }
        }
        return bizName;
    }
}
