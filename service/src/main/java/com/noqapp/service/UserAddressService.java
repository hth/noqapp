package com.noqapp.service;

import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonUserAddress;
import com.noqapp.domain.json.JsonUserAddressList;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.repository.UserAddressManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 5/16/18 9:18 AM
 */
@Service
public class UserAddressService {

    private UserAddressManager userAddressManager;
    private ExternalService externalService;

    @Autowired
    public UserAddressService(UserAddressManager userAddressManager, ExternalService externalService) {
        this.userAddressManager = userAddressManager;
        this.externalService = externalService;
    }

    @Mobile
    public UserAddressEntity saveAddress(String id, String qid, String address) {
        Asserts.check(StringUtils.isNotBlank(id), "Id cannot be blank");

        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(address), 0);
        UserAddressEntity userAddress = new UserAddressEntity(qid, address)
            .setLastUsed()
            .setCountryShortName(decodedAddress.getCountryShortName())
            .setGeoHash(decodedAddress.getGeoPoint() == null ? null : decodedAddress.getGeoPoint().getGeohash());
        userAddress.setId(id);
        userAddressManager.save(userAddress);
        return userAddress;
    }

    @Mobile
    @Async
    public void deleteAddress(String id, String qid) {
        Asserts.check(StringUtils.isNotBlank(id), "Id cannot be blank");
        userAddressManager.deleteAddress(id, qid);
    }

    public List<UserAddressEntity> getAll(String qid) {
        return userAddressManager.getAll(qid);
    }

    @Mobile
    public JsonUserAddressList getAllAsJson(String qid) {
        JsonUserAddressList jsonUserAddressList = new JsonUserAddressList();
        List<UserAddressEntity> userAddresses = getAll(qid);
        for (UserAddressEntity userAddress : userAddresses) {
            jsonUserAddressList.addJsonUserAddresses(new JsonUserAddress()
                .setId(userAddress.getId())
                .setAddress(userAddress.getAddress())
                .setGeoHash(userAddress.getGeoHash())
                .setCountryShortName(userAddress.getCountryShortName()));
        }

        return jsonUserAddressList;
    }

    @Mobile
    @Async
    public void addressLastUsed(String address, String qid) {
        userAddressManager.updateLastUsedAddress(address, qid);
    }
}
