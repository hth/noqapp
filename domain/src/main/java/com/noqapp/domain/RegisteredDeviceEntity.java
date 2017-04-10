package com.noqapp.domain;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.DeviceTypeEnum;

import java.io.File;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 3/1/17 12:20 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Mobile
@Document (collection = "REGISTERED_DEVICE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "registered_device_did_idx", def = "{'DID': -1}", unique = true),
        @CompoundIndex (name = "registered_device_token_idx", def = "{'TK': -1}", unique = false),
        @CompoundIndex (name = "registered_device_rid_idx", def = "{'RID': -1}", unique = false, sparse = true)
})
public class RegisteredDeviceEntity extends BaseEntity {

    private static final String TOPICS = "/topics";
    private static final String SEPARATOR = File.separator;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("DID")
    private String deviceId;

    @NotNull
    @Field ("DT")
    private DeviceTypeEnum deviceType;

    /** FCM token for sending push notification. */
    @Field ("TK")
    private String token;

    /** To keep bean happy. */
    private RegisteredDeviceEntity() {
        super();
    }

    private RegisteredDeviceEntity(String receiptUserId, String deviceId, DeviceTypeEnum deviceType, String token) {
        super();
        this.receiptUserId = receiptUserId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.token = token;
    }

    private RegisteredDeviceEntity(String deviceId, DeviceTypeEnum deviceType, String token) {
        super();
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.token = token;
    }

    public static RegisteredDeviceEntity newInstance(String receiptUserId, String deviceId, DeviceTypeEnum deviceType, String token) {
        if (StringUtils.isBlank(receiptUserId)) {
            return new RegisteredDeviceEntity(deviceId, deviceType, token);
        } else {
            return new RegisteredDeviceEntity(receiptUserId, deviceId, deviceType, token);
        }
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Transient
    public String getTopic() {
        return TOPICS + SEPARATOR + token;
    }
}
