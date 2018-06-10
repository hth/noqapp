package com.noqapp.view.form;

import com.noqapp.domain.types.GenderEnum;

/**
 * hitender
 * 6/10/18 2:19 AM
 */
public class UserProfileForm {

    private String profileImage;
    private String email;
    private String firstName;
    private String lastName;
    private String address;

    private String phone;
    private String timeZone;
    private boolean emailValidated;
    private boolean phoneValidated;

    private GenderEnum gender;
    private String birthday;

    public String getProfileImage() {
        return profileImage;
    }

    public UserProfileForm setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserProfileForm setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserProfileForm setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserProfileForm setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserProfileForm setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserProfileForm setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public UserProfileForm setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public UserProfileForm setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
        return this;
    }

    public boolean isPhoneValidated() {
        return phoneValidated;
    }

    public UserProfileForm setPhoneValidated(boolean phoneValidated) {
        this.phoneValidated = phoneValidated;
        return this;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public UserProfileForm setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public UserProfileForm setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }
}

