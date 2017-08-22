package com.noqapp.domain.flow;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import com.noqapp.utils.Formatter;

import java.io.Serializable;

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

    private String gender;
    private String birthday;
    private String password;
    private boolean accountExists;
    private boolean acceptsAgreement;

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

    public RegisterUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public RegisterUser setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public RegisterUser setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public RegisterUser setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public RegisterUser setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public RegisterUser setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterUser setPassword(String password) {
        this.password = password;
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

    public RegisterUser setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
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

    public RegisterUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
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

