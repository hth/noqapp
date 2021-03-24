package com.noqapp.service;

import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonUserAddress;
import com.noqapp.domain.json.JsonUserAddressList;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.repository.UserAddressManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * hitender
 * 5/16/18 9:18 AM
 */
@Service
public class UserAddressService {
    private UserAddressManager userAddressManager;
    private UserProfileManager userProfileManager;

    @Autowired
    public UserAddressService(UserAddressManager userAddressManager, UserProfileManager userProfileManager) {
        this.userAddressManager = userAddressManager;
        this.userProfileManager = userProfileManager;
    }

    @Mobile
    public UserAddressEntity saveAddress(String id, String qid, JsonUserAddress jsonUserAddress) {
       return saveAddress(
           id,
           qid,
           jsonUserAddress.getAddress(),
           jsonUserAddress.getArea(),
           jsonUserAddress.getTown(),
           jsonUserAddress.getDistrict(),
           jsonUserAddress.getState(),
           jsonUserAddress.getStateShortName(),
           jsonUserAddress.getCountryShortName(),
           jsonUserAddress.geoHash(),
           jsonUserAddress.getCoordinate());
    }

    private UserAddressEntity saveAddress(
        String id,
        String qid,
        String address,
        String area,
        String town,
        String district,
        String state,
        String stateShortName,
        String countryShortName,
        String geoHash,
        double[] coordinate
    ) {
        Assert.hasText(id, "Id cannot be blank");

        long existing = userAddressManager.count(qid);
        UserAddressEntity userAddress = new UserAddressEntity(
            qid,
            address,
            area,
            town,
            district,
            state,
            stateShortName,
            countryShortName,
            geoHash,
            coordinate
        ).setLastUsed();
        userAddress.setId(id);
        userAddressManager.save(userAddress);

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
        if (StringUtils.isBlank(userProfile.getAddress()) || 0 == existing) {
            userProfile
                .setAddress(address)
                .setCountryShortName(countryShortName)
                .setAddressOrigin(AddressOriginEnum.G);
            userProfileManager.save(userProfile);
        }
        return userAddress;
    }

    @Mobile
    @Async
    public void markAddressPrimary(String id, String qid) {
        Assert.hasText(id, "Id cannot be blank");
        UserAddressEntity userAddress = userAddressManager.markAddressPrimary(id, qid);
    }

    @Mobile
    @Async
    public void deleteAddress(String id, String qid) {
        Assert.hasText(id, "Id cannot be blank");
        userAddressManager.deleteAddress(id, qid);
    }

    private List<UserAddressEntity> getAll(String qid) {
        return userAddressManager.getAll(qid);
    }

    @Mobile
    public JsonUserAddressList getAllAsJson(String qid) {
        JsonUserAddressList jsonUserAddressList = new JsonUserAddressList();
        List<UserAddressEntity> userAddresses = getAll(qid);
        for (UserAddressEntity userAddress : userAddresses) {
            jsonUserAddressList.addJsonUserAddresses(JsonUserAddress.populateAsJson(userAddress));
        }

        return jsonUserAddressList;
    }

    @Mobile
    @Async
    public void addressLastUsed(String id, String qid) {
        if (StringUtils.isNotBlank(id)) {
            userAddressManager.updateLastUsedAddress(id, qid);
        }
    }

    public UserAddressEntity findById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return userAddressManager.findById(id);
    }

    public UserAddressEntity findByAddress(String qid, String address) {
        return userAddressManager.findByAddress(qid, address);
    }
}
