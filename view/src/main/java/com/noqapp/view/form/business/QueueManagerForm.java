package com.noqapp.view.form.business;

import com.noqapp.domain.UserProfileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 6/30/17 2:54 PM
 */
public class QueueManagerForm {
    private String queueName;
    private List<UserProfileEntity> userProfiles = new ArrayList<>();

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public List<UserProfileEntity> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(List<UserProfileEntity> userProfiles) {
        this.userProfiles = userProfiles;
    }
}
