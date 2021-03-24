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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(UserAddressService.class);
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

    public UserAddressEntity updateAddress(String id, String qid, JsonUserAddress jsonUserAddress) {
        UserAddressEntity userAddressFromDB = userAddressManager.findById(id);
        UserAddressEntity userAddress = new UserAddressEntity(
            qid,
            jsonUserAddress.getAddress(),
            jsonUserAddress.getArea(),
            jsonUserAddress.getTown(),
            jsonUserAddress.getDistrict(),
            jsonUserAddress.getState(),
            jsonUserAddress.getStateShortName(),
            jsonUserAddress.getCountryShortName(),
            jsonUserAddress.geoHash(),
            jsonUserAddress.getCoordinate()
        ).setLastUsed();
        userAddress.setId(id);

        userAddress.setVersion(userAddressFromDB.getVersion());
        userAddress.setCreated(userAddressFromDB.getCreated());
        userAddressManager.save(userAddress);

        return userAddress;
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

        if (!userAddressManager.doesAddressWithGoeHashExists(qid, geoHash)) {
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
            LOG.info("Saved added successfully {} {}", userAddress.getId(), userAddress.getQueueUserId());
            return userAddress;
        } else {
            UserAddressEntity userAddress = userAddressManager.findOne(qid, geoHash);
            LOG.info("Skipped added successfully {} {}", userAddress.getId(), userAddress.getQueueUserId());
            return userAddress;
        }
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
            jsonUserAddressList.addJsonUserAddresses(
                new JsonUserAddress()
                    .setId(userAddress.getId())
                    .setAddress(userAddress.getAddress())
                    .setArea(userAddress.getArea())
                    .setTown(userAddress.getTown())
                    .setDistrict(userAddress.getDistrict())
                    .setState(userAddress.getState())
                    .setStateShortName(userAddress.getStateShortName())
                    .setCountryShortName(userAddress.getCountryShortName())
                    .setGeoHash(userAddress.getGeoHash())
                    .setLatitude(String.valueOf(userAddress.getCoordinate()[1]))
                    .setLongitude(String.valueOf(userAddress.getCoordinate()[0])));
        }

        return jsonUserAddressList;
    }

    @Mobile
    @Async
    public void addressLastUsed(String address, String qid) {
        userAddressManager.updateLastUsedAddress(address, qid);
    }

    public List<UserAddressEntity> findAllWhereCoordinateDoesNotExists() {
        return userAddressManager.findAllWhereCoordinateDoesNotExists();
    }
}
