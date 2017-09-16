package com.noqapp.domain.flow;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.utils.Formatter;
import com.noqapp.utils.ScrubbedInput;

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
    private String countryShortName;
    private String phone;
    private String timeZone;
    private boolean emailValidated;
    private boolean phoneValidated;
    private AddressOriginEnum addressOrigin;

    private String gender;
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

    public String getGender() {
        return gender;
    }

    public RegisterUser setGender(ScrubbedInput gender) {
        this.gender = gender.getText();
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
        return Formatter.phoneCleanup(phone);
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

    public void setTimeZone(ScrubbedInput timeZone) {
        this.timeZone = timeZone.getText();
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("queueUserId", queueUserId)
                .append("email", email)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("address", address)
                .append("countryShortName", countryShortName)
                .append("phone", phone)
                .append("timeZone", timeZone)
                .append("emailValidated", emailValidated)
                .append("phoneValidated", phoneValidated)
                .append("gender", gender)
                .append("birthday", birthday)
                .append("password", password)
                .append("accountExists", accountExists)
                .append("acceptsAgreement", acceptsAgreement)
                .toString();
    }
}

