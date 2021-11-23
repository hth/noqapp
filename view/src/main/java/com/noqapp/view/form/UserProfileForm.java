package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.types.GenderEnum;

/**
 * hitender
 * 6/10/18 2:19 AM
 */
public class UserProfileForm {

    private String profileImage;
    private ScrubbedInput email;
    private ScrubbedInput firstName;
    private ScrubbedInput lastName;
    private ScrubbedInput address;

    private ScrubbedInput phone;
    private ScrubbedInput timeZone;
    private boolean emailValidated;
    private boolean phoneValidated;

    private GenderEnum gender;
    private ScrubbedInput birthday;

    private UserPreferenceEntity userPreference;
    private long reviewPointsEarned;
    private long totalInvitePointsEarned;

    public String getProfileImage() {
        return profileImage;
    }

    public UserProfileForm setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public ScrubbedInput getEmail() {
        return email;
    }

    public UserProfileForm setEmail(ScrubbedInput email) {
        this.email = email;
        return this;
    }

    public ScrubbedInput getFirstName() {
        return firstName;
    }

    public UserProfileForm setFirstName(ScrubbedInput firstName) {
        this.firstName = firstName;
        return this;
    }

    public ScrubbedInput getLastName() {
        return lastName;
    }

    public UserProfileForm setLastName(ScrubbedInput lastName) {
        this.lastName = lastName;
        return this;
    }

    public ScrubbedInput getAddress() {
        return address;
    }

    public UserProfileForm setAddress(ScrubbedInput address) {
        this.address = address;
        return this;
    }

    public ScrubbedInput getPhone() {
        return phone;
    }

    public UserProfileForm setPhone(ScrubbedInput phone) {
        this.phone = phone;
        return this;
    }

    public ScrubbedInput getTimeZone() {
        return timeZone;
    }

    public UserProfileForm setTimeZone(ScrubbedInput timeZone) {
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

    public ScrubbedInput getBirthday() {
        return birthday;
    }

    public UserProfileForm setBirthday(ScrubbedInput birthday) {
        this.birthday = birthday;
        return this;
    }

    public UserPreferenceEntity getUserPreference() {
        return userPreference;
    }

    public UserProfileForm setUserPreference(UserPreferenceEntity userPreference) {
        this.userPreference = userPreference;
        return this;
    }

    public long getReviewPointsEarned() {
        return reviewPointsEarned;
    }

    public UserProfileForm setReviewPointsEarned(long reviewPointsEarned) {
        this.reviewPointsEarned = reviewPointsEarned;
        return this;
    }

    public long getTotalInvitePointsEarned() {
        return totalInvitePointsEarned;
    }

    public UserProfileForm setTotalInvitePointsEarned(long totalInvitePointsEarned) {
        this.totalInvitePointsEarned = totalInvitePointsEarned;
        return this;
    }
}
