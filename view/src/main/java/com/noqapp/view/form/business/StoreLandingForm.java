package com.noqapp.view.form.business;

import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

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
    private String categoryName;
    private String qrFileName;
    private List<StoreHourEntity> storeHours;
    private BusinessTypeEnum businessType;

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

    public String getCategoryName() {
        return categoryName;
    }

    public StoreLandingForm setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public StoreLandingForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }
}
