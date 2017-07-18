package com.noqapp.repository;

import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.DeviceTypeEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 3/1/17 12:26 PM
 */
public interface RegisteredDeviceManager extends RepositoryManager<RegisteredDeviceEntity> {

    @Mobile
    RegisteredDeviceEntity find(String rid, String did);

    RegisteredDeviceEntity findFCMToken(String rid, String did);

    List<RegisteredDeviceEntity> findAll(String rid);

    /**
     * Update the token for a particular device id. Update token every time the request comes in.
     *
     * @param rid
     * @param did
     * @param token
     * @return
     */
    @SuppressWarnings ("unused")
    @Mobile
    RegisteredDeviceEntity lastAccessed(String rid, String did, String token);

    /**
     * When existing did, update with latest info. This happens when one user logs out and another user
     * logs in without deleting the app.
     *
     * @param did
     * @param rid
     * @param deviceType
     * @param token
     */
    @Mobile
    boolean resetRegisteredDeviceWithNewDetails(String did, String rid, DeviceTypeEnum deviceType, String token);
}
