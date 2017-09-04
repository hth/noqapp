package com.noqapp.domain.helper;

import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;

import java.util.Date;

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
    private String address;
    private String email;
    private String queueUserId;
    private UserLevelEnum userLevel;
    private boolean active;
    private Date created;
    private BusinessUserRegistrationStatusEnum businessUserRegistrationStatus;

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
        return phone;
    }

    public QueueSupervisor setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public QueueSupervisor setAddress(String address) {
        this.address = address;
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
}
