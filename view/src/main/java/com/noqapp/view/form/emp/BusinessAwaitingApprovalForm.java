package com.noqapp.view.form.emp;

import org.springframework.data.annotation.Transient;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 12/11/16 1:58 PM
 */
public class BusinessAwaitingApprovalForm {

    private BusinessUserEntity businessUser;
    private UserProfileEntity userProfile;
    private UserProfileEntity inviteeUserProfile;

    @Transient
    private List<BusinessTypeEnum> availableBusinessTypes = BusinessTypeEnum.asList();

    public BusinessUserEntity getBusinessUser() {
        return businessUser;
    }

    public BusinessAwaitingApprovalForm setBusinessUser(BusinessUserEntity businessUser) {
        this.businessUser = businessUser;
        return this;
    }

    public UserProfileEntity getUserProfile() {
        return userProfile;
    }

    public BusinessAwaitingApprovalForm setUserProfile(UserProfileEntity userProfile) {
        this.userProfile = userProfile;
        return this;
    }

    public UserProfileEntity getInviteeUserProfile() {
        return inviteeUserProfile;
    }

    public BusinessAwaitingApprovalForm setInviteeUserProfile(UserProfileEntity inviteeUserProfile) {
        this.inviteeUserProfile = inviteeUserProfile;
        return this;
    }

    public List<BusinessTypeEnum> getAvailableBusinessTypes() {
        return availableBusinessTypes;
    }
}
