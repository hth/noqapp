package com.token.domain.flow;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;

import com.token.domain.BusinessUserEntity;
import com.token.domain.types.BusinessTypeEnum;
import com.token.utils.CommonUtil;

import java.io.Serializable;
import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 4:36 PM
 */
public class RegisterBusiness implements Serializable {

    private String name;
    /** Business types are initialized in flow. Why? Show off. */
    private List<BusinessTypeEnum> businessTypes;
    private String address;
    private String phone;
    private String countryShortName;
    private BusinessUserEntity businessUser;

    private boolean multiStore = false;
    private boolean businessSameAsStore = false;

    private String displayName;
    private String addressStore;
    private String phoneStore;
    private String countryShortNameStore;
    private int startHourStore;
    private int endHourStore;
    private int tokenAvailableFrom;

    @Transient
    private List<BusinessTypeEnum> availableBusinessTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BusinessTypeEnum> getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(List<BusinessTypeEnum> businessTypes) {
        this.businessTypes = businessTypes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        if (StringUtils.isNotBlank(phone)) {
            return CommonUtil.phoneFormatter(phone, countryShortName);
        } else {
            return phone;
        }
    }

    @Transient
    public String getBusinessPhoneNotFormatted() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public BusinessUserEntity getBusinessUser() {
        return businessUser;
    }

    public void setBusinessUser(BusinessUserEntity businessUser) {
        this.businessUser = businessUser;
    }

    public List<BusinessTypeEnum> getAvailableBusinessTypes() {
        return availableBusinessTypes;
    }

    public void setAvailableBusinessTypes(List<BusinessTypeEnum> availableBusinessTypes) {
        this.availableBusinessTypes = availableBusinessTypes;
    }

    public boolean isMultiStore() {
        return multiStore;
    }

    public void setMultiStore(boolean multiStore) {
        this.multiStore = multiStore;
    }

    public boolean isBusinessSameAsStore() {
        return businessSameAsStore;
    }

    public void setBusinessSameAsStore(boolean businessSameAsStore) {
        this.businessSameAsStore = businessSameAsStore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAddressStore() {
        return addressStore;
    }

    public void setAddressStore(String addressStore) {
        this.addressStore = addressStore;
    }

    public String getPhoneStore() {
        return phoneStore;
    }

    @Transient
    public String getStorePhoneNotFormatted() {
        if (StringUtils.isNotBlank(phoneStore)) {
            return CommonUtil.phoneFormatter(phoneStore, countryShortName);
        } else {
            return phoneStore;
        }
    }

    public void setPhoneStore(String phoneStore) {
        this.phoneStore = phoneStore;
    }

    public String getCountryShortNameStore() {
        return countryShortNameStore;
    }

    public void setCountryShortNameStore(String countryShortNameStore) {
        this.countryShortNameStore = countryShortNameStore;
    }

    public int getStartHourStore() {
        return startHourStore;
    }

    public void setStartHourStore(int startHourStore) {
        this.startHourStore = startHourStore;
    }

    public int getEndHourStore() {
        return endHourStore;
    }

    public void setEndHourStore(int endHourStore) {
        this.endHourStore = endHourStore;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public void setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
    }
}
