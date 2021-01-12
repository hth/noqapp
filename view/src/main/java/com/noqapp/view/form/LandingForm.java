package com.noqapp.view.form;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.market.PropertyEntity;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 12/10/16 4:37 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class LandingForm {

    private BusinessUserRegistrationStatusEnum businessUserRegistrationStatus;
    private Date businessAccountSignedUp;
    private List<QueueEntity> currentQueues;
    private List<QueueEntity> historicalQueues;
    private List<PropertyEntity> properties;

    private List<UserProfileEntity> minorUserProfiles;

    public BusinessUserRegistrationStatusEnum getBusinessUserRegistrationStatus() {
        return businessUserRegistrationStatus;
    }

    public LandingForm setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        this.businessUserRegistrationStatus = businessUserRegistrationStatus;
        return this;
    }

    public Date getBusinessAccountSignedUp() {
        return businessAccountSignedUp;
    }

    public LandingForm setBusinessAccountSignedUp(Date businessAccountSignedUp) {
        this.businessAccountSignedUp = businessAccountSignedUp;
        return this;
    }

    public List<QueueEntity> getCurrentQueues() {
        return currentQueues;
    }

    public LandingForm setCurrentQueues(List<QueueEntity> currentQueues) {
        this.currentQueues = currentQueues;
        return this;
    }

    public List<QueueEntity> getHistoricalQueues() {
        return historicalQueues;
    }

    public LandingForm setHistoricalQueues(List<QueueEntity> historicalQueues) {
        this.historicalQueues = historicalQueues;
        return this;
    }

    public List<PropertyEntity> getProperties() {
        return properties;
    }

    public LandingForm setProperties(List<PropertyEntity> properties) {
        this.properties = properties;
        return this;
    }

    public List<UserProfileEntity> getMinorUserProfiles() {
        return minorUserProfiles;
    }

    public LandingForm setMinorUserProfiles(List<UserProfileEntity> minorUserProfiles) {
        this.minorUserProfiles = minorUserProfiles;
        return this;
    }
}
