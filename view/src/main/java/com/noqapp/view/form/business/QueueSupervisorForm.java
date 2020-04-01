package com.noqapp.view.form.business;

import com.noqapp.domain.helper.QueueSupervisor;
import com.noqapp.domain.types.BusinessTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 6/30/17 2:54 PM
 */
public class QueueSupervisorForm {
    private String bizStoreId;
    private String queueName;
    private BusinessTypeEnum businessType;
    private List<QueueSupervisor> queueSupervisors = new ArrayList<>();
    private List<QueueSupervisor> availableQueueSupervisor = new ArrayList<>();

    public String getBizStoreId() {
        return bizStoreId;
    }

    public void setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public QueueSupervisorForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public List<QueueSupervisor> getQueueSupervisors() {
        return queueSupervisors;
    }

    public QueueSupervisorForm setQueueSupervisors(List<QueueSupervisor> queueSupervisors) {
        this.queueSupervisors = queueSupervisors;
        return this;
    }

    public List<QueueSupervisor> getAvailableQueueSupervisor() {
        return availableQueueSupervisor;
    }

    public QueueSupervisorForm setAvailableQueueSupervisor(List<QueueSupervisor> availableQueueSupervisor) {
        this.availableQueueSupervisor = availableQueueSupervisor;
        return this;
    }
}
