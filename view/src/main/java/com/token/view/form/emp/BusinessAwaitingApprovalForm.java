package com.token.view.form.emp;

import org.springframework.data.annotation.Transient;

import com.token.domain.BusinessUserEntity;
import com.token.domain.UserProfileEntity;
import com.token.domain.types.BusinessTypeEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 12/11/16 1:58 PM
 */
public class BusinessAwaitingApprovalForm {

    private BusinessUserEntity businessUser;
    private UserProfileEntity userProfile;

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

    public List<BusinessTypeEnum> getAvailableBusinessTypes() {
        return availableBusinessTypes;
    }
}
