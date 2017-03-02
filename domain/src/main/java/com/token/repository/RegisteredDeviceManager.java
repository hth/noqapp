package com.token.repository;

import com.token.domain.RegisteredDeviceEntity;
import com.token.domain.annotation.Mobile;
import com.token.domain.types.DeviceTypeEnum;

/**
 * User: hitender
 * Date: 3/1/17 12:26 PM
 */
public interface RegisteredDeviceManager extends RepositoryManager<RegisteredDeviceEntity> {

    /**
     * Find if device is registered with receipt user.
     *
     * @param rid
     * @param did
     * @return
     */
    @Mobile
    RegisteredDeviceEntity find(String rid, String did);

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

    /**
     * Finds or register device if not found. When not found returns false and saves the new device.
     * When found returns true.
     *
     * @param rid
     * @param did
     * @return
     */
    @SuppressWarnings ("unused")
    @Mobile
    RegisteredDeviceEntity registerDevice(String rid, String did, DeviceTypeEnum deviceType, String token);
}
