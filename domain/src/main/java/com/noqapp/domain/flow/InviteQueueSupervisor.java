package com.noqapp.domain.flow;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.BusinessTypeEnum;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 7/14/17 12:23 PM
 */
public class InviteQueueSupervisor implements Serializable {

    private String bizStoreId;
    private String countryShortName;
    private int countryCode;
    private String phoneNumber;
    private ScrubbedInput inviteeCode;
    private BusinessTypeEnum businessType;
    private ScrubbedInput doctor;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public InviteQueueSupervisor setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public InviteQueueSupervisor setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public InviteQueueSupervisor setCountryCode(int countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public InviteQueueSupervisor setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public ScrubbedInput getInviteeCode() {
        return inviteeCode;
    }

    public InviteQueueSupervisor setInviteeCode(ScrubbedInput inviteeCode) {
        this.inviteeCode = inviteeCode;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public InviteQueueSupervisor setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public ScrubbedInput getDoctor() {
        return doctor;
    }

    public InviteQueueSupervisor setDoctor(ScrubbedInput doctor) {
        this.doctor = doctor;
        return this;
    }
}
