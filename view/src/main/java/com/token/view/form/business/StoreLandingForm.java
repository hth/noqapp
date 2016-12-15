package com.token.view.form.business;

/**
 * User: hitender
 * Date: 12/15/16 9:11 AM
 */
public class StoreLandingForm {

    private String address;
    private String phone;
    private String displayName;
    private String qrFileName;

    public String getAddress() {
        return address;
    }

    public StoreLandingForm setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public StoreLandingForm setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public StoreLandingForm setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getQrFileName() {
        return qrFileName;
    }

    public StoreLandingForm setQrFileName(String qrFileName) {
        this.qrFileName = qrFileName;
        return this;
    }
}
