package com.token.domain.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.token.domain.BizStoreEntity;
import com.token.domain.BusinessUserEntity;

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
        getRegisterUser().setRid(businessUser.getReceiptUserId());
        if (null != businessUser.getBizName()) {
            getRegisterBusiness().setName(businessUser.getBizName().getBusinessName());
            getRegisterBusiness().setBusinessTypes(businessUser.getBizName().getBusinessTypes());
        }

        if (null != bizStore) {
            getRegisterBusiness().setAddress(bizStore.getAddress());
            getRegisterBusiness().setPhone(bizStore.getPhone());
            getRegisterBusiness().setCountryShortName(bizStore.getCountryShortName());
        }
    }

    public static MigrateToBusinessRegistration newInstance(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        return new MigrateToBusinessRegistration(businessUser, bizStore);
    }
}