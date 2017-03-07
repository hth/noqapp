package com.token.domain.json.fcm.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.token.domain.types.QueueStatusEnum;

/**
 * User: hitender
 * Date: 1/1/17 7:06 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonTopicData implements JsonData {

    @JsonProperty ("message")
    private String message;

    @JsonProperty("ln")
    private int lastNumber;

    @JsonProperty ("cs")
    private int currentlyServing;

    @JsonProperty ("c")
    private String codeQR;

    @JsonProperty ("q")
    private QueueStatusEnum queueStatus;

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
}
