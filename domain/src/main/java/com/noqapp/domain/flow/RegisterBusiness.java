package com.noqapp.domain.flow;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.ScrubbedInput;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 4:36 PM
 */
public class RegisterBusiness implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(RegisterBusiness.class);

    private String bizId;
    private String name;
    /* Business types are initialized in flow. Why? Show off. */
    private List<BusinessTypeEnum> businessTypes;
    private String address;
    private String countryShortName;
    private String phone;
    private String timeZone;
    /* Reference to person who has recommended business. */
    private String inviteeCode;
    private AddressOriginEnum addressOrigin;
    private BusinessUserEntity businessUser;

    private boolean multiStore = false;
    private boolean businessSameAsStore = false;

    private String bizStoreId;
    private String displayName;
    private String addressStore;
    private String countryShortNameStore;
    private String phoneStore;
    private String timeZoneStore;
    private AddressOriginEnum addressStoreOrigin;
    private boolean remoteJoin;
    private boolean allowLoggedInUser;
    private List<BusinessHour> businessHours = new LinkedList<>();

    private HashMap<String, DecodedAddress> foundAddresses = new LinkedHashMap<>();
    private String foundAddressPlaceId;
    private boolean selectFoundAddress;

    private HashMap<String, DecodedAddress> foundAddressStores = new LinkedHashMap<>();
    private String foundAddressStorePlaceId;
    private boolean selectFoundAddressStore;

    public RegisterBusiness() {
        for (int i = 1; i <= 7; i++) {
            BusinessHour businessHour = new BusinessHour(DayOfWeek.of(i));
            businessHours.add(businessHour);
        }
    }

    @Transient
    private List<BusinessTypeEnum> availableBusinessTypes;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getName() {
        return name;
    }

    public void setName(ScrubbedInput name) {
        this.name = name.getText();
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

    public void setAddress(ScrubbedInput address) {
        /* Java 8 regex engine supports \R which represents any line separator. */
        this.address = address.getText().replaceAll("\\R", " ");
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(ScrubbedInput countryShortName) {
        this.countryShortName = countryShortName.getText();
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

    public void setPhone(ScrubbedInput phone) {
        this.phone = phone.getText();
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ScrubbedInput timeZone) {
        this.timeZone = timeZone.getText();
    }

    public String getInviteeCode() {
        return inviteeCode;
    }

    public void setInviteeCode(String inviteeCode) {
        this.inviteeCode = inviteeCode;
    }

    public AddressOriginEnum getAddressOrigin() {
        return addressOrigin;
    }

    public RegisterBusiness setAddressOrigin(AddressOriginEnum addressOrigin) {
        this.addressOrigin = addressOrigin;
        return this;
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

    public String getBizStoreId() {
        return bizStoreId;
    }

    public void setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(ScrubbedInput displayName) {
        this.displayName = displayName.getText();
    }

    public String getAddressStore() {
        return addressStore;
    }

    public void setAddressStore(ScrubbedInput addressStore) {
        /* Java 8 regex engine supports \R which represents any line separator. */
        this.addressStore = addressStore.getText().replaceAll("\\R", " ");
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

    public void setPhoneStore(ScrubbedInput phoneStore) {
        this.phoneStore = phoneStore.getText();
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

    public void setCountryShortNameStore(ScrubbedInput countryShortNameStore) {
        this.countryShortNameStore = countryShortNameStore.getText();
    }

    public String getTimeZoneStore() {
        return timeZoneStore;
    }

    public void setTimeZoneStore(ScrubbedInput timeZoneStore) {
        this.timeZoneStore = timeZoneStore.getText();
    }

    public AddressOriginEnum getAddressStoreOrigin() {
        return addressStoreOrigin;
    }

    public RegisterBusiness setAddressStoreOrigin(AddressOriginEnum addressStoreOrigin) {
        this.addressStoreOrigin = addressStoreOrigin;
        return this;
    }

    public boolean isRemoteJoin() {
        return remoteJoin;
    }

    public RegisterBusiness setRemoteJoin(boolean remoteJoin) {
        this.remoteJoin = remoteJoin;
        return this;
    }

    public boolean isAllowLoggedInUser() {
        return allowLoggedInUser;
    }

    public void setAllowLoggedInUser(boolean allowLoggedInUser) {
        this.allowLoggedInUser = allowLoggedInUser;
    }

    public List<BusinessHour> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(List<BusinessHour> businessHours) {
        this.businessHours = businessHours;
    }

    public HashMap<String, DecodedAddress> getFoundAddresses() {
        return foundAddresses;
    }

    public RegisterBusiness setFoundAddresses(HashMap<String, DecodedAddress> foundAddresses) {
        this.foundAddresses = foundAddresses;
        return this;
    }

    public String getFoundAddressPlaceId() {
        return foundAddressPlaceId;
    }

    public RegisterBusiness setFoundAddressPlaceId(String foundAddressPlaceId) {
        this.foundAddressPlaceId = foundAddressPlaceId;
        return this;
    }

    public boolean isSelectFoundAddress() {
        return selectFoundAddress;
    }

    public RegisterBusiness setSelectFoundAddress(boolean selectFoundAddress) {
        this.selectFoundAddress = selectFoundAddress;
        return this;
    }

    public HashMap<String, DecodedAddress> getFoundAddressStores() {
        return foundAddressStores;
    }

    public RegisterBusiness setFoundAddressStores(HashMap<String, DecodedAddress> foundAddressStores) {
        this.foundAddressStores = foundAddressStores;
        return this;
    }

    public String getFoundAddressStorePlaceId() {
        return foundAddressStorePlaceId;
    }

    public RegisterBusiness setFoundAddressStorePlaceId(String foundAddressStorePlaceId) {
        this.foundAddressStorePlaceId = foundAddressStorePlaceId;
        return this;
    }

    public boolean isSelectFoundAddressStore() {
        return selectFoundAddressStore;
    }

    public RegisterBusiness setSelectFoundAddressStore(boolean selectFoundAddressStore) {
        this.selectFoundAddressStore = selectFoundAddressStore;
        return this;
    }

    @Transient
    public String computeWebLocation(String town, String stateShortName) {
        try {
            String townString = StringUtils.isNotBlank(town) ? town.trim().toLowerCase().replace(" ", "-") : "-";
            String stateShortNameString = StringUtils.isNotBlank(stateShortName) ? stateShortName.trim().toLowerCase() : "-";

            String webLocation = "/"
                    + countryShortNameStore.toLowerCase()
                    + "/"
                    + name.trim().toLowerCase().replace(" ", "-")
                    + "/"
                    + townString
                    + "-"
                    + stateShortNameString
                    + "/"
                    + displayName.trim().toLowerCase().replace(" ", "-");

            /*
             * Since empty townString and stateShortNameString can contain '-',
             * hence replacing two consecutive '-' with a blank.
             */
            return webLocation.replaceAll("--", "");
        } catch (Exception e) {
            LOG.error("Failed creating Web Location for store at town={} stateShortName={}", town, stateShortName);
            throw e;
        }
    }

    @Override
    public String toString() {
        return "RegisterBusiness{" +
                "bizId='" + bizId + '\'' +
                ", name='" + name + '\'' +
                ", businessTypes=" + businessTypes +
                ", address='" + address + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", phone='" + phone + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", addressOrigin=" + addressOrigin +
                ", businessUser=" + businessUser +
                ", multiStore=" + multiStore +
                ", businessSameAsStore=" + businessSameAsStore +
                ", bizStoreId='" + bizStoreId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", addressStore='" + addressStore + '\'' +
                ", countryShortNameStore='" + countryShortNameStore + '\'' +
                ", phoneStore='" + phoneStore + '\'' +
                ", timeZoneStore='" + timeZoneStore + '\'' +
                ", addressStoreOrigin=" + addressStoreOrigin +
                ", remoteJoin=" + remoteJoin +
                ", allowLoggedInUser=" + allowLoggedInUser +
                ", businessHours=" + businessHours +
                ", foundAddresses=" + foundAddresses +
                ", foundAddressPlaceId='" + foundAddressPlaceId + '\'' +
                ", selectFoundAddress=" + selectFoundAddress +
                ", foundAddressStores=" + foundAddressStores +
                ", foundAddressStorePlaceId='" + foundAddressStorePlaceId + '\'' +
                ", selectFoundAddressStore=" + selectFoundAddressStore +
                ", availableBusinessTypes=" + availableBusinessTypes +
                '}';
    }
}
