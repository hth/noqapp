package com.noqapp.domain.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.utils.ScrubbedInput;

import java.io.Serializable;

/**
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
            getRegisterBusiness().setName(new ScrubbedInput(businessUser.getBizName().getBusinessName()));
            getRegisterBusiness().setBusinessTypes(businessUser.getBizName().getBusinessTypes());
        }

        if (null != bizStore) {
            getRegisterBusiness().setAddress(new ScrubbedInput(bizStore.getAddress()));
            getRegisterBusiness().setPhone(new ScrubbedInput(bizStore.getPhone()));
            getRegisterBusiness().setCountryShortName(new ScrubbedInput(bizStore.getCountryShortName()));
        }
    }

    public static MigrateToBusinessRegistration newInstance(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        return new MigrateToBusinessRegistration(businessUser, bizStore);
    }
}