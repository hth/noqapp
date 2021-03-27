package com.noqapp.service;

import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonUserAddress;
import com.noqapp.domain.json.JsonUserAddressList;
import com.noqapp.repository.UserAddressManager;

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

    @Autowired
    public UserAddressService(UserAddressManager userAddressManager) {
        this.userAddressManager = userAddressManager;
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
        Assert.hasText(id, "Id cannot be blank " + qid);
        long countActiveRecords = userAddressManager.countActive(qid);
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

        /* When there is no other address, then the current address should be marked as primary by default. */
        if (0 == countActiveRecords) {
            markAddressPrimary(userAddress.getId(), userAddress.getQueueUserId());
        }
        return userAddress;
    }

    @Mobile
    @Async
    public void markAddressPrimary(String id, String qid) {
        Assert.hasText(id, "Id cannot be blank " + qid);
        userAddressManager.markAddressPrimary(id, qid);
    }

    @Mobile
    @Async
    public void markAddressAsInactive(String id, String qid) {
        Assert.hasText(id, "Id cannot be blank " + qid);
        userAddressManager.markAddressAsInactive(id, qid);
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
    public void updateLastUsedAddress(String id, String qid) {
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

    /** Needs to clean up when UserProfile address is removed. */
    public UserAddressEntity findOneUserAddress(String qid, String address) {
        String userAddressId;
        if (StringUtils.isNotBlank(address)) {
            UserAddressEntity userAddress = findByAddress(qid, address);
            if (null != userAddress) {
                return userAddress;
            }
        }

        List<UserAddressEntity> userAddresses = getAll(qid);
        for (UserAddressEntity userAddress : userAddresses) {
            if (userAddress.isPrimaryAddress()) {
                return userAddress;
            }
        }

        return userAddresses.isEmpty() ? null : userAddresses.get(0);
    }

    public UserAddressEntity findOneUserAddress(List<UserAddressEntity> userAddresses) {
        for (UserAddressEntity userAddress : userAddresses) {
            if (userAddress.isPrimaryAddress()) {
                return userAddress;
            }
        }

        return userAddresses.isEmpty() ? null : userAddresses.get(0);
    }

    public UserAddressEntity findOneUserAddress(String qid) {
        return findOneUserAddress(getAll(qid));
    }
}
