package com.noqapp.view.form.business;

import com.noqapp.domain.json.JsonPurchaseOrderList;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.types.BusinessTypeEnum;

/**
 * hitender
 * 3/6/18 3:29 AM
 */
public class InQueueForm {
    private String queueName;
    private BusinessTypeEnum businessType;
    private String codeQR;
    private JsonQueuePersonList jsonQueuePersonList;
    private JsonPurchaseOrderList jsonPurchaseOrderList;

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

    public JsonPurchaseOrderList getJsonPurchaseOrderList() {
        return jsonPurchaseOrderList;
    }

    public InQueueForm setJsonPurchaseOrderList(JsonPurchaseOrderList jsonPurchaseOrderList) {
        this.jsonPurchaseOrderList = jsonPurchaseOrderList;
        return this;
    }
}
