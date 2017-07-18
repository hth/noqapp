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

    public void setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getInviteeCode() {
        return inviteeCode;
    }

    public void setInviteeCode(String inviteeCode) {
        this.inviteeCode = inviteeCode;
    }
}
