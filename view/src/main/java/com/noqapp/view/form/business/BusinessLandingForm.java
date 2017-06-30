package com.noqapp.view.form.business;

import com.noqapp.domain.BizStoreEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 12/7/16 11:52 PM
 */
public class BusinessLandingForm {

    private String bizName;
    private List<BizStoreEntity> bizStores;
    private Map<String, Long> assignedUsers = new HashMap<>();

    public String getBizName() {
        return bizName;
    }

    public BusinessLandingForm setBizName(String bizName) {
        this.bizName = bizName;
        return this;
    }

    public List<BizStoreEntity> getBizStores() {
        return bizStores;
    }

    public BusinessLandingForm setBizStores(List<BizStoreEntity> bizStores) {
        this.bizStores = bizStores;
        return this;
    }

    public Map<String, Long> getAssignedUsers() {
        return assignedUsers;
    }

    public void addAssignedUsers(String id, Long assignedToQueue) {
        this.assignedUsers.put(id, assignedToQueue);
    }
}
