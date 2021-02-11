package com.noqapp.domain;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.AppFlavorEnum;
import com.noqapp.domain.types.DeviceTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * User: hitender
 * Date: 3/1/17 12:20 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Mobile
@Document(collection = "REGISTERED_DEVICE")
@CompoundIndexes(value = {
    @CompoundIndex(name = "registered_device_did_idx", def = "{'DID': -1}", unique = true),
    @CompoundIndex(name = "registered_device_token_idx", def = "{'TK': -1}", unique = false),
    @CompoundIndex(name = "registered_device_qid_idx", def = "{'QID': -1}", unique = false, sparse = true)
})
public class RegisteredDeviceEntity extends BaseEntity {

    private static final String TOPICS = "/topics";
    private static final String SEPARATOR = File.separator;

    @Field("QID")
    private String queueUserId;

    @Field("DID")
    private String deviceId;

    @Field("DT")
    private DeviceTypeEnum deviceType;

    @Field("AF")
    private AppFlavorEnum appFlavor;

    /** FCM token for sending push notification. */
    @Field("TK")
    private String token;

    @Field("MO")
    private String model;

    @Field("OS")
    private String osVersion;

    @Field("AV")
    private String appVersion;

    @Field("CT")
    private String cityName;

    /* Format Longitude and then Latitude. */
    @Field("COR")
    private double[] coordinate;

    /* Format Longitude and then Latitude. */
    @Field("CH")
    private Set<double[]> coordinateHistorical = new HashSet<>();

    @Field("IP")
    private String ipAddress;

    @Field("IH")
    private Set<String> ipAddressHistorical = new HashSet<>();

    @Field("SB")
    private boolean sinceBeginning = true;

    /** To keep bean happy. */
    public RegisteredDeviceEntity() {
        super();
    }

    private RegisteredDeviceEntity(
        String queueUserId,
        String deviceId,
        DeviceTypeEnum deviceType,
        AppFlavorEnum appFlavor,
        String token,
        String appVersion,
        String cityName,
        double[] coordinate,
        String ipAddress
    ) {
        super();
        this.queueUserId = queueUserId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.appFlavor = appFlavor;
        this.token = token;
        this.appVersion = appVersion;
        this.cityName = cityName;
        this.coordinate = coordinate;
        this.ipAddress = ipAddress;
    }

    private RegisteredDeviceEntity(
        String deviceId,
        DeviceTypeEnum deviceType,
        AppFlavorEnum appFlavor,
        String token,
        String appVersion,
        String cityName,
        double[] coordinate,
        String ipAddress
    ) {
        super();
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.appFlavor = appFlavor;
        this.token = token;
        this.appVersion = appVersion;
        this.cityName = cityName;
        this.coordinate = coordinate;
        this.ipAddress = ipAddress;
    }

    public static RegisteredDeviceEntity newInstance(
        String queueUserId,
        String deviceId,
        DeviceTypeEnum deviceType,
        AppFlavorEnum appFlavor,
        String token,
        String appVersion,
        String cityName,
        double[] coordinate,
        String ipAddress
    ) {
        if (StringUtils.isBlank(queueUserId)) {
            return new RegisteredDeviceEntity(deviceId, deviceType, appFlavor, token, appVersion, cityName, coordinate, ipAddress);
        } else {
            return new RegisteredDeviceEntity(queueUserId, deviceId, deviceType, appFlavor, token, appVersion, cityName, coordinate, ipAddress);
        }
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public void setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public DeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceTypeEnum deviceType) {
        this.deviceType = deviceType;
    }

    public AppFlavorEnum getAppFlavor() {
        return appFlavor;
    }

    public RegisteredDeviceEntity setAppFlavor(AppFlavorEnum appFlavor) {
        this.appFlavor = appFlavor;
        return this;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getModel() {
        return model;
    }

    public RegisteredDeviceEntity setModel(String model) {
        this.model = model;
        return this;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public RegisteredDeviceEntity setOsVersion(String osVersion) {
        this.osVersion = osVersion;
        return this;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public RegisteredDeviceEntity setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public RegisteredDeviceEntity setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public RegisteredDeviceEntity setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public Set<double[]> getCoordinateHistorical() {
        return coordinateHistorical;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public RegisteredDeviceEntity setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public Set<String> getIpAddressHistorical() {
        return ipAddressHistorical;
    }

    public boolean isSinceBeginning() {
        return sinceBeginning;
    }

    public RegisteredDeviceEntity setSinceBeginning(boolean sinceBeginning) {
        this.sinceBeginning = sinceBeginning;
        return this;
    }
}
