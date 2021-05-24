package com.noqapp.repository;

import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.AppFlavorEnum;
import com.noqapp.domain.types.DeviceTypeEnum;

import org.springframework.data.geo.GeoResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 3/1/17 12:26 PM
 */
public interface RegisteredDeviceManager extends RepositoryManager<RegisteredDeviceEntity> {

    @Mobile
    RegisteredDeviceEntity find(String qid, String did);

    @Mobile
    boolean updateDevice(
        String id,
        String did,
        String qid,
        DeviceTypeEnum deviceType,
        AppFlavorEnum appFlavor,
        String token,
        String model,
        String osVersion,
        String deviceLanguage,
        String cityName,
        double[] coordinate,
        String ipAddress,
        boolean sinceBeginning);

    @Mobile
    List<RegisteredDeviceEntity> findAll(String qid, String did);

    RegisteredDeviceEntity findFCMToken(String qid, String did);

    List<RegisteredDeviceEntity> findAll(String qid);

    /**
     * Update the token for a particular device id. Update token every time the request comes in.
     *
     * @param qid
     * @param did
     * @param token
     * @return
     */
    @SuppressWarnings("unused")
    @Mobile
    RegisteredDeviceEntity lastAccessed(String qid, String did, String token, String model, String osVersion, String appVersion, String ipAddress, String deviceLanguage, String cityName);

    /**
     * When existing did, update with latest info. This happens when one user logs out and another user
     * logs in without deleting the app.
     */
    @Mobile
    boolean resetRegisteredDeviceWithNewDetails(
        String did,
        String qid,
        DeviceTypeEnum deviceType,
        AppFlavorEnum appFlavor,
        String token,
        String model,
        String osVersion,
        String deviceLanguage,
        String cityName,
        double[] coordinate,
        String ipAddress);

    /**
     * When data is fetched since beginning. This helps set to prevent fetching from beginning going forward.
     *
     * @param id
     */
    @Mobile
    void markFetchedSinceBeginningForDevice(String id);

    /**
     * Called when user logs out of the App.
     *
     * @param id
     */
    @Mobile
    void unsetQidForDevice(String id);

    long countRegisteredBetweenDates(Date from, Date to, DeviceTypeEnum deviceType);
    long countRegisteredBetweenDates(Date from, Date to, DeviceTypeEnum deviceType, AppFlavorEnum appFlavor);

    RegisteredDeviceEntity findRecentDevice(String qid);

    Stream<RegisteredDeviceEntity> findAllTokenWithoutQID(AppFlavorEnum appFlavor);

    RegisteredDeviceEntity findByDid(String deviceId);

    @Mobile
    void updateRegisteredDevice(String did, String qid, DeviceTypeEnum deviceType, boolean sinceBeginning);

    List<RegisteredDeviceEntity> findAlmostObsoleteDevices();

    Stream<GeoResult<RegisteredDeviceEntity>> findDevicesWithinVicinity(double[] coordinate, int distanceToPropagateInformation);
}
