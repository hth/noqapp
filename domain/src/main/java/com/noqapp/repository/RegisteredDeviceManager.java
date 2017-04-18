package com.noqapp.repository;

import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 3/1/17 12:26 PM
 */
public interface RegisteredDeviceManager extends RepositoryManager<RegisteredDeviceEntity> {

    @Mobile
    RegisteredDeviceEntity find(String rid, String did);

    String findFCMToken(String rid, String did);

    /**
     * If updates are available then return device and mark the device as inactive else return null
     *
     * @param rid
     * @param did
     * @return
     */
    @SuppressWarnings ("unused")
    @Mobile
    RegisteredDeviceEntity lastAccessed(String rid, String did);

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
}
