package com.noqapp.domain.helper;

import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User: hitender
 * Date: 7/19/17 9:09 PM
 */
public class QueueSupervisor {

    private String businessUserId;
    private String storeId;
    private String businessId;
    private String name;
    private String phone;
    private boolean phoneValidated;
    private String address;
    private String countryShortName;
    private String email;
    private String queueUserId;
    private UserLevelEnum userLevel;
    private boolean active;
    private Date created;
    private BusinessUserRegistrationStatusEnum businessUserRegistrationStatus;
    private BusinessTypeEnum businessType;
    private Set<NameDatePair> educations = new LinkedHashSet<>();
    private Set<NameDatePair> licenses = new LinkedHashSet<>();

    public String getBusinessUserId() {
        return businessUserId;
    }

    public QueueSupervisor setBusinessUserId(String businessUserId) {
        this.businessUserId = businessUserId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }

    public QueueSupervisor setStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getBusinessId() {
        return businessId;
    }

    public QueueSupervisor setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    public String getName() {
        return name;
    }

    public QueueSupervisor setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return Formatter.phoneInternationalFormat(phone, countryShortName);
    }

    public QueueSupervisor setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public boolean isPhoneValidated() {
        return phoneValidated;
    }

    public QueueSupervisor setPhoneValidated(boolean phoneValidated) {
        this.phoneValidated = phoneValidated;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public QueueSupervisor setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public QueueSupervisor setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public QueueSupervisor setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public QueueSupervisor setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }

    public QueueSupervisor setUserLevel(UserLevelEnum userLevel) {
        this.userLevel = userLevel;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public QueueSupervisor setActive(boolean active) {
        this.active = active;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public QueueSupervisor setCreated(Date created) {
        this.created = created;
        return this;
    }

    public BusinessUserRegistrationStatusEnum getBusinessUserRegistrationStatus() {
        return businessUserRegistrationStatus;
    }

    public QueueSupervisor setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        this.businessUserRegistrationStatus = businessUserRegistrationStatus;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public QueueSupervisor setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public Set<NameDatePair> getEducations() {
        return educations;
    }

    public QueueSupervisor setEducations(Set<NameDatePair> educations) {
        this.educations = educations;
        return this;
    }

    public Set<NameDatePair> getLicenses() {
        return licenses;
    }

    public QueueSupervisor setLicenses(Set<NameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueSupervisor that = (QueueSupervisor) o;
        return Objects.equals(businessId, that.businessId) &&
                Objects.equals(queueUserId, that.queueUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessId, queueUserId);
    }
}
