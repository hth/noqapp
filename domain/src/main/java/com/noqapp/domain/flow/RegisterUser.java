package com.noqapp.domain.flow;

import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.GenderEnum;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * User: hitender
 * Date: 11/23/16 4:35 PM
 */
public class RegisterUser implements Serializable {
    private String queueUserId;
    private String email;
    private String firstName;
    private String lastName;
    private String address;

    /* Just to keep track of older address supplied against the new address. */
    private String placeHolderAddress;
    private String countryShortName;
    private String phone;
    private String timeZone;
    private boolean emailValidated;
    private boolean phoneValidated;
    private AddressOriginEnum addressOrigin;

    private GenderEnum gender;
    private String birthday;
    private String password;
    private boolean accountExists;
    private boolean acceptsAgreement;

    /* Are just place holders for data setup for address provided. */
    private HashMap<String, DecodedAddress> foundAddresses = new LinkedHashMap<>();
    private String foundAddressPlaceId;
    private boolean selectFoundAddress;
    /* End of place holder of data. */

    public String getQueueUserId() {
        return queueUserId;
    }

    public RegisterUser setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RegisterUser setEmail(ScrubbedInput email) {
        this.email = email.getText();
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public RegisterUser setFirstName(ScrubbedInput firstName) {
        this.firstName = firstName.getText();
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public RegisterUser setLastName(ScrubbedInput lastName) {
        this.lastName = lastName.getText();
        return this;
    }

    public String getAddress() {
        return address;
    }

    public RegisterUser setAddress(ScrubbedInput address) {
        /* Java 8 regex engine supports \R which represents any line separator. */
        this.address = address.getText().replaceAll("\\R", " ");
        return this;
    }

    public String getPlaceHolderAddress() {
        return placeHolderAddress;
    }

    public void setPlaceHolderAddress(String placeHolderAddress) {
        this.placeHolderAddress = placeHolderAddress;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public RegisterUser setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public RegisterUser setBirthday(ScrubbedInput birthday) {
        this.birthday = birthday.getText();
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterUser setPassword(ScrubbedInput password) {
        this.password = password.getText();
        return this;
    }

    public boolean isAccountExists() {
        return accountExists;
    }

    public RegisterUser setAccountExists(boolean accountExists) {
        this.accountExists = accountExists;
        return this;
    }

    public boolean isAcceptsAgreement() {
        return acceptsAgreement;
    }

    public RegisterUser setAcceptsAgreement(boolean acceptsAgreement) {
        this.acceptsAgreement = acceptsAgreement;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public RegisterUser setCountryShortName(ScrubbedInput countryShortName) {
        this.countryShortName = countryShortName.getText();
        return this;
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
        String phoneRaw = null;
        if (StringUtils.isNotBlank(countryShortName)) {
            int countryCode = Formatter.findCountryCodeFromCountryShortCode(countryShortName);
            phoneRaw = phone.replaceFirst(String.valueOf(countryCode), "");
        }
        return Formatter.phoneCleanup(StringUtils.isBlank(phoneRaw) ? phone : phoneRaw);
    }

    /* Return phone as is. */
    @Transient
    public String getPhoneAsIs() {
        return phone;
    }

    @Transient
    public String getPhoneWithCountryCode() {
        Assert.notNull(countryShortName, "Country code cannot be null");
        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(countryShortName)) {
            return Formatter.phoneNumberWithCountryCode(Formatter.phoneCleanup(phone), countryShortName);
        }

        return null;
    }

    public RegisterUser setPhone(ScrubbedInput phone) {
        this.phone = phone.getText();
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public RegisterUser setTimeZone(ScrubbedInput timeZone) {
        this.timeZone = timeZone.getText();
        return this;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public RegisterUser setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
        return this;
    }

    public boolean isPhoneValidated() {
        return phoneValidated;
    }

    public RegisterUser setPhoneValidated(boolean phoneValidated) {
        this.phoneValidated = phoneValidated;
        return this;
    }

    public AddressOriginEnum getAddressOrigin() {
        return addressOrigin;
    }

    public RegisterUser setAddressOrigin(AddressOriginEnum addressOrigin) {
        this.addressOrigin = addressOrigin;
        return this;
    }

    public HashMap<String, DecodedAddress> getFoundAddresses() {
        return foundAddresses;
    }

    public RegisterUser setFoundAddresses(HashMap<String, DecodedAddress> foundAddresses) {
        this.foundAddresses = foundAddresses;
        return this;
    }

    public String getFoundAddressPlaceId() {
        return foundAddressPlaceId;
    }

    public RegisterUser setFoundAddressPlaceId(String foundAddressPlaceId) {
        this.foundAddressPlaceId = foundAddressPlaceId;
        return this;
    }

    public boolean isSelectFoundAddress() {
        return selectFoundAddress;
    }

    public RegisterUser setSelectFoundAddress(boolean selectFoundAddress) {
        this.selectFoundAddress = selectFoundAddress;
        return this;
    }

    @Transient
    public String getName() {
        if (StringUtils.isNotBlank(lastName)) {
            return StringUtils.trim(firstName + UserAccountEntity.BLANK_SPACE + lastName);
        }

        return firstName;
    }

    @Transient
    public boolean hasUserEnteredAddressChanged() {
        return !address.equalsIgnoreCase(placeHolderAddress);
    }

    @Override
    public String toString() {
        return "RegisterUser{" +
                "queueUserId='" + queueUserId + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", placeHolderAddress='" + placeHolderAddress + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", phone='" + phone + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", emailValidated=" + emailValidated +
                ", phoneValidated=" + phoneValidated +
                ", addressOrigin=" + addressOrigin +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", password='" + password + '\'' +
                ", accountExists=" + accountExists +
                ", acceptsAgreement=" + acceptsAgreement +
                ", foundAddresses=" + foundAddresses +
                ", foundAddressPlaceId='" + foundAddressPlaceId + '\'' +
                ", selectFoundAddress=" + selectFoundAddress +
                '}';
    }
}

