package com.noqapp.domain.flow;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;

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
            BizNameEntity bizName = businessUser.getBizName();

            getRegisterBusiness().setBizId(bizName.getId());
            getRegisterBusiness().setName(new ScrubbedInput(bizName.getBusinessName()));
            getRegisterBusiness().setAddress(new ScrubbedInput(bizName.getAddress()));
            getRegisterBusiness().setArea(new ScrubbedInput(bizName.getArea()));
            getRegisterBusiness().setTown(new ScrubbedInput(bizName.getTown()));
            getRegisterBusiness().setCountryShortName(new ScrubbedInput(bizName.getCountryShortName()));
            getRegisterBusiness().setPhone(new ScrubbedInput(bizName.getPhone()));
            getRegisterBusiness().setTimeZone(new ScrubbedInput(bizName.getTimeZone()));
            getRegisterBusiness().setInviteeCode(bizName.getInviteeCode());
            getRegisterBusiness().setAddressOrigin(bizName.getAddressOrigin());
            getRegisterBusiness().setFoundAddressPlaceId(bizName.getPlaceId());
            getRegisterBusiness().setBusinessType(bizName.getBusinessType());
            getRegisterBusiness().setFacilities(bizName.getFacilities());
            getRegisterBusiness().setAmenities(bizName.getAmenities());
            getRegisterBusiness().setDayClosed(bizName.isDayClosed());
            getRegisterBusiness().setClaimed(bizName.isClaimed());
        }

        if (null != bizStore) {
            getRegisterBusiness().setBizStoreId(bizStore.getId());
            getRegisterBusiness().setDisplayName(new ScrubbedInput(bizStore.getDisplayName()));
            getRegisterBusiness().setAddressStore(new ScrubbedInput(bizStore.getAddress()));
            getRegisterBusiness().setAreaStore(new ScrubbedInput(bizStore.getArea()));
            getRegisterBusiness().setTownStore(new ScrubbedInput(bizStore.getTown()));
            getRegisterBusiness().setPhoneStore(new ScrubbedInput(bizStore.getPhone()));
            getRegisterBusiness().setRemoteJoin(bizStore.isRemoteJoin());
            getRegisterBusiness().setAllowLoggedInUser(bizStore.isAllowLoggedInUser());
            getRegisterBusiness().setCountryShortName(new ScrubbedInput(bizStore.getCountryShortName()));
            getRegisterBusiness().setFacilitiesStore(bizStore.getFacilities());
            getRegisterBusiness().setAmenitiesStore(bizStore.getAmenities());
        }
    }

    public static MigrateToBusinessRegistration newInstance(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        return new MigrateToBusinessRegistration(businessUser, bizStore);
    }
}