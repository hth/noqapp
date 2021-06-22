package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 3/1/17 12:40 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class DeviceService {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    private RegisteredDeviceManager registeredDeviceManager;
    private UserProfileManager userProfileManager;

    @Autowired
    public DeviceService(
        RegisteredDeviceManager registeredDeviceManager,
        UserProfileManager userProfileManager
    ) {
        this.registeredDeviceManager = registeredDeviceManager;
        this.userProfileManager = userProfileManager;
    }

    @Mobile
    public RegisteredDeviceEntity findRecentDevice(String qid) {
        return registeredDeviceManager.findRecentDevice(qid);
    }

    @Mobile
    public RegisteredDeviceEntity findByDid(String did) {
        return registeredDeviceManager.findByDid(did);
    }

    public String getExistingDeviceId(String qid, String notUserDeviceId) {
        return getExistingDeviceId(registeredDeviceManager.findRecentDevice(qid), notUserDeviceId);
    }

    @Mobile
    public static String getExistingDeviceId(RegisteredDeviceEntity registeredDevice, String notUserDeviceId) {
        if (null == registeredDevice) {
            return CommonUtil.appendRandomToDeviceId(notUserDeviceId);
        } else {
            return registeredDevice.getDeviceId();
        }
    }

    @Mobile
    public RegisteredDeviceEntity findRegisteredDeviceByQid(String qid) {
        return findDeviceByUserProfile(userProfileManager.findByQueueUserId(qid));
    }

    private RegisteredDeviceEntity findDeviceByUserProfile(UserProfileEntity userProfile) {
        RegisteredDeviceEntity registeredDevice;
        if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
            String guardianQid = userProfileManager.findOneByPhone(userProfile.getGuardianPhone()).getQueueUserId();
            registeredDevice = findRecentDevice(guardianQid);
        } else {
            registeredDevice = findRecentDevice(userProfile.getQueueUserId());
        }

        return registeredDevice;
    }

    public Stream<GeoResult<RegisteredDeviceEntity>> findInProximity(GeoJsonPoint point, double distanceInMeters) {
        return registeredDeviceManager.findInProximity(point, distanceInMeters);
    }
}
