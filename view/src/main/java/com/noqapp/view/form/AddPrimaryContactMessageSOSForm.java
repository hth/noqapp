package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * hitender
 * 5/25/21 2:51 PM
 */
public class AddPrimaryContactMessageSOSForm implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(AddPrimaryContactMessageSOSForm.class);

    private String countryShortName = "IN";
    private String phoneNumber;
    private ScrubbedInput inviteeCode;

    public String getCountryShortName() {
        return countryShortName;
    }

    public AddPrimaryContactMessageSOSForm setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public AddPrimaryContactMessageSOSForm setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public ScrubbedInput getInviteeCode() {
        return inviteeCode;
    }

    public AddPrimaryContactMessageSOSForm setInviteeCode(ScrubbedInput inviteeCode) {
        this.inviteeCode = inviteeCode;
        return this;
    }
}
