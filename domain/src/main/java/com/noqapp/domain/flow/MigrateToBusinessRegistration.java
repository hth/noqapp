package com.noqapp.domain.flow;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.utils.ScrubbedInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Populates flow for business registration.
 *
 * User: hitender
 * Date: 12/9/16 1:31 PM
 */
public class MigrateToBusinessRegistration extends Register implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateToBusinessRegistration.class);
    private static final long serialVersionUID = -6047892968409443583L;

    private MigrateToBusinessRegistration(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        getRegisterBusiness().setBusinessUser(businessUser);
        getRegisterUser().setQueueUserId(businessUser.getQueueUserId());
        if (null != businessUser.getBizName()) {
            getRegisterBusiness().setBizId(businessUser.getBizName().getId());
            getRegisterBusiness().setName(new ScrubbedInput(businessUser.getBizName().getBusinessName()));
            getRegisterBusiness().setAddress(new ScrubbedInput(businessUser.getBizName().getAddress()));
            getRegisterBusiness().setPhone(new ScrubbedInput(businessUser.getBizName().getPhone()));
            getRegisterBusiness().setBusinessTypes(businessUser.getBizName().getBusinessTypes());
            getRegisterBusiness().setMultiStore(businessUser.getBizName().isMultiStore());
            getRegisterBusiness().setInviteeCode(businessUser.getBizName().getInviteeCode());
        }

        if (null != bizStore) {
            getRegisterBusiness().setBizStoreId(bizStore.getId());
            getRegisterBusiness().setDisplayName(new ScrubbedInput(bizStore.getDisplayName()));
            getRegisterBusiness().setAddressStore(new ScrubbedInput(bizStore.getAddress()));
            getRegisterBusiness().setPhoneStore(new ScrubbedInput(bizStore.getPhone()));
            getRegisterBusiness().setRemoteJoin(bizStore.isRemoteJoin());
            getRegisterBusiness().setAllowLoggedInUser(bizStore.isAllowLoggedInUser());
            getRegisterBusiness().setCountryShortName(new ScrubbedInput(bizStore.getCountryShortName()));
        }
    }

    public static MigrateToBusinessRegistration newInstance(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        return new MigrateToBusinessRegistration(businessUser, bizStore);
    }
}