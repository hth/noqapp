package com.noqapp.view.form.business;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.types.BusinessTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 3/6/18 3:29 AM
 */
public class InQueueForm {
    private String queueName;
    private BusinessTypeEnum businessType;
    private String codeQR;
    private JsonQueuePersonList jsonQueuePersonList;
    private List<PurchaseOrderEntity> purchaseOrders = new ArrayList<>();

    public String getQueueName() {
        return queueName;
    }

    public InQueueForm setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public InQueueForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public InQueueForm setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public JsonQueuePersonList getJsonQueuePersonList() {
        return jsonQueuePersonList;
    }

    public InQueueForm setJsonQueuePersonList(JsonQueuePersonList jsonQueuePersonList) {
        this.jsonQueuePersonList = jsonQueuePersonList;
        return this;
    }

    public List<PurchaseOrderEntity> getPurchaseOrders() {
        return purchaseOrders;
    }

    public InQueueForm setPurchaseOrders(List<PurchaseOrderEntity> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
        return this;
    }
}
