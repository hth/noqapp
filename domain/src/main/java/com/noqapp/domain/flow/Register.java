package com.noqapp.domain.flow;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 11/23/16 4:35 PM
 */
public class Register implements Serializable {

    private RegisterUser registerUser = new RegisterUser();
    private RegisterBusiness registerBusiness = new RegisterBusiness();
    private String autoApproveBusinessTurnedOn = "OFF";

    public RegisterUser getRegisterUser() {
        return registerUser;
    }

    public RegisterBusiness getRegisterBusiness() {
        return registerBusiness;
    }

    public String getAutoApproveBusinessTurnedOn() {
        return autoApproveBusinessTurnedOn;
    }

    public Register setAutoApproveBusinessTurnedOn(String autoApproveBusinessTurnedOn) {
        this.autoApproveBusinessTurnedOn = autoApproveBusinessTurnedOn;
        return this;
    }

    @Override
    public String toString() {
        return "Register{" +
                "registerUser=" + registerUser +
                ", registerBusiness=" + registerBusiness +
                '}';
    }
}
