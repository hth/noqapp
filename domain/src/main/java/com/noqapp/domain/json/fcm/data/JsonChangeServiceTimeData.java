package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.json.JsonQueueChangeServiceTime;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 10/29/20 1:09 PM
 */
public class JsonChangeServiceTimeData extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("qcsts")
    private List<JsonQueueChangeServiceTime> jsonQueueChangeServiceTimes = new LinkedList<>();

    public JsonChangeServiceTimeData(FirebaseMessageTypeEnum firebaseMessageType, MessageOriginEnum messageOrigin) {
        super(firebaseMessageType);
        this.messageOrigin = messageOrigin;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonChangeServiceTimeData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonChangeServiceTimeData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public List<JsonQueueChangeServiceTime> getJsonQueueChangeServiceTimes() {
        return jsonQueueChangeServiceTimes;
    }

    public JsonChangeServiceTimeData addJsonQueueChangeServiceTimes(JsonQueueChangeServiceTime jsonQueueChangeServiceTime) {
        this.jsonQueueChangeServiceTimes.add(jsonQueueChangeServiceTime);
        return this;
    }
}
