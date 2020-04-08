package com.noqapp.view.form.business;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.helper.QueueDetail;
import com.noqapp.domain.json.JsonTopic;
import com.noqapp.domain.types.BusinessTypeEnum;

import org.springframework.data.annotation.Transient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 12/7/16 11:52 PM
 */
public class BusinessLandingForm {

    private String bizName;
    private BusinessTypeEnum businessType;
    private String bizCodeQR;
    private List<BizStoreEntity> bizStores;
    private Map<String, QueueDetail> queueDetails = new HashMap<>();
    private Map<String, String>  categories = new HashMap<>();

    /* Used when Queue Supervisor logs in. */
    private List<JsonTopic> jsonTopics;

    public String getBizName() {
        return bizName;
    }

    public BusinessLandingForm setBizName(String bizName) {
        this.bizName = bizName;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public BusinessLandingForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBizCodeQR() {
        return bizCodeQR;
    }

    public BusinessLandingForm setBizCodeQR(String bizCodeQR) {
        this.bizCodeQR = bizCodeQR;
        return this;
    }

    public List<BizStoreEntity> getBizStores() {
        return bizStores;
    }

    public BusinessLandingForm setBizStores(List<BizStoreEntity> bizStores) {
        this.bizStores = bizStores;
        return this;
    }

    public Map<String, QueueDetail> getQueueDetails() {
        return queueDetails;
    }

    public BusinessLandingForm addQueueDetail(QueueDetail queueDetail) {
        this.queueDetails.put(queueDetail.getId(), queueDetail);
        return this;
    }

    public List<JsonTopic> getJsonTopics() {
        return jsonTopics;
    }

    public BusinessLandingForm setJsonTopics(List<JsonTopic> jsonTopics) {
        this.jsonTopics = jsonTopics;
        return this;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public BusinessLandingForm setCategories(Map<String, String> categories) {
        this.categories = categories;
        return this;
    }

    @Transient
    public String getCorrectLabelBasedOnBusinessType() {
        switch (businessType.getMessageOrigin()) {
            case O:
                return "Store";
            case Q:
            default:
                return "Queue";
        }
    }
}
