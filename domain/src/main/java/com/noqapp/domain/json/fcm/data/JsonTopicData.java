package com.noqapp.domain.json.fcm.data;

import com.fasterxml.jackson.annotation.*;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;

/**
 * User: hitender
 * Date: 1/1/17 7:06 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonTopicData extends JsonData {

    @JsonProperty("message")
    private String message;

    @JsonProperty("ln")
    private int lastNumber;

    @JsonProperty("cs")
    private int currentlyServing;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("g")
    private String goTo;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    public JsonTopicData(FirebaseMessageTypeEnum firebaseMessageType) {
        super(firebaseMessageType);
    }

    public String getMessage() {
        return message;
    }

    public JsonTopicData setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public JsonTopicData setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public int getCurrentlyServing() {
        return currentlyServing;
    }

    public JsonTopicData setCurrentlyServing(int currentlyServing) {
        this.currentlyServing = currentlyServing;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonTopicData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonTopicData setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getGoTo() {
        return goTo;
    }

    public JsonTopicData setGoTo(String goTo) {
        this.goTo = goTo;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonTopicData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }
}
