package com.noqapp.domain.flow;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.utils.Formatter;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.LinkedList;
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
    private String countryShortName;
    private String phone;
    private String timeZone;
    private BusinessUserEntity businessUser;

    private boolean multiStore = false;
    private boolean businessSameAsStore = false;

    private String displayName;
    private String addressStore;
    private String countryShortNameStore;
    private String phoneStore;
    private String timeZoneStore;
    private List<BusinessHour> businessHours = new LinkedList<>();

    public RegisterBusiness() {
        for (int i = 1; i <= 7; i++) {
            BusinessHour businessHour = new BusinessHour(DayOfWeek.of(i));
            businessHours.add(businessHour);
        }
    }

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

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public String getPhone() {
        if (StringUtils.isNotBlank(phone)) {
            return Formatter.phoneFormatter(phone, countryShortName);
        } else {
            return phone;
        }
    }

    @Transient
    public String getPhoneNotFormatted() {
        return Formatter.phoneCleanup(phone);
    }

    @Transient
    public String getPhoneWithCountryCode() {
        Assert.notNull(countryShortName, "Country code cannot be null");
        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(countryShortName)) {
            return Formatter.phoneNumberWithCountryCode(Formatter.phoneCleanup(phone), countryShortName);
        }

        return null;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
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
        if (StringUtils.isNotBlank(phoneStore)) {
            return Formatter.phoneFormatter(phoneStore, countryShortName);
        } else {
            return phoneStore;
        }
    }

    @Transient
    public String getPhoneStoreNotFormatted() {
        return Formatter.phoneCleanup(phoneStore);
    }

    public void setPhoneStore(String phoneStore) {
        this.phoneStore = phoneStore;
    }

    @Transient
    public String getPhoneStoreWithCountryCode() {
        Assert.notNull(countryShortNameStore, "Country code cannot be null");
        if (StringUtils.isNotBlank(phoneStore) && StringUtils.isNotBlank(countryShortNameStore)) {
            return Formatter.phoneNumberWithCountryCode(Formatter.phoneCleanup(phoneStore), countryShortNameStore);
        }

        return null;
    }

    public String getCountryShortNameStore() {
        return countryShortNameStore;
    }

    public void setCountryShortNameStore(String countryShortNameStore) {
        this.countryShortNameStore = countryShortNameStore;
    }

    public String getTimeZoneStore() {
        return timeZoneStore;
    }

    public void setTimeZoneStore(String timeZoneStore) {
        this.timeZoneStore = timeZoneStore;
    }

    public List<BusinessHour> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(List<BusinessHour> businessHours) {
        this.businessHours = businessHours;
    }

    @Transient
    public String computeWebLocation(String town, String stateShortName) {
        return "/" + countryShortNameStore.toLowerCase() + "/" + name + "/" + town.toLowerCase() + "-" + stateShortName.toLowerCase() + "/" + displayName;
    }
}
