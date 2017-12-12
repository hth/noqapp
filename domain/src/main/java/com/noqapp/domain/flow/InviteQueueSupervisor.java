package com.noqapp.domain.flow;

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
    private String inviteeCode;

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

    public String getInviteeCode() {
        return inviteeCode;
    }

    public InviteQueueSupervisor setInviteeCode(String inviteeCode) {
        this.inviteeCode = inviteeCode;
        return this;
    }
}
