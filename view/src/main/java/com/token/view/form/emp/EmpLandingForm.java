package com.token.view.form.emp;

import com.token.domain.BusinessUserEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 12/11/16 8:46 AM
 */
public class EmpLandingForm {
    private long awaitingApprovalCount;
    private List<BusinessUserEntity> businessUsers;

    public long getAwaitingApprovalCount() {
        return awaitingApprovalCount;
    }

    public EmpLandingForm setAwaitingApprovalCount(long awaitingApprovalCount) {
        this.awaitingApprovalCount = awaitingApprovalCount;
        return this;
    }

    public List<BusinessUserEntity> getBusinessUsers() {
        return businessUsers;
    }

    public EmpLandingForm setBusinessUsers(List<BusinessUserEntity> businessUsers) {
        this.businessUsers = businessUsers;
        return this;
    }
}
