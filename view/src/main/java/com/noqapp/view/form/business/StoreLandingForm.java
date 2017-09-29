package com.noqapp.view.form.business;

import com.noqapp.domain.StoreHourEntity;

import java.util.List;

/**
 * Used to display store details on web page.
 *
 * User: hitender
 * Date: 12/15/16 9:11 AM
 */
public class StoreLandingForm {

    private String businessName;
    private String address;
    private String phone;
    private String displayName;
    private String qrFileName;
    List<StoreHourEntity> storeHours;

    public String getBusinessName() {
        return businessName;
    }

    public StoreLandingForm setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

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

    public List<StoreHourEntity> getStoreHours() {
        return storeHours;
    }

    public StoreLandingForm setStoreHours(List<StoreHourEntity> storeHours) {
        this.storeHours = storeHours;
        return this;
    }
}
