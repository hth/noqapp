package com.token.domain.flow;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 11/23/16 4:35 PM
 */
public class Register implements Serializable {

    private RegisterUser registerUser = new RegisterUser();
    private RegisterBusiness registerBusiness = new RegisterBusiness();

    public RegisterUser getRegisterUser() {
        return registerUser;
    }

    public RegisterBusiness getRegisterBusiness() {
        return registerBusiness;
    }
}
