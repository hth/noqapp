package com.noqapp.domain.json;

import com.noqapp.domain.TokenQueueEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2/26/17 5:13 PM
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
@JsonInclude (JsonInclude.Include.NON_NULL)
public class JsonTopic extends JsonToken {

    @JsonProperty ("o")
    private String topic;

    @JsonProperty("hour")
    private JsonHour hour;

    @JsonProperty("dv")
    private JsonDataVisibility jsonDataVisibility;

    @JsonProperty("pp")
    private JsonPaymentPermission jsonPaymentPermission;

    private JsonTopic() {
        super();
    }

    public JsonTopic(TokenQueueEntity tokenQueue) {
        super(tokenQueue);
        this.topic = tokenQueue.getTopic();
    }

    public String getTopic() {
        return topic;
    }

    public JsonHour getHour() {
        return hour;
    }

    public JsonTopic setHour(JsonHour hour) {
        this.hour = hour;
        return this;
    }

    public JsonDataVisibility getJsonDataVisibility() {
        return jsonDataVisibility;
    }

    public JsonTopic setJsonDataVisibility(JsonDataVisibility jsonDataVisibility) {
        this.jsonDataVisibility = jsonDataVisibility;
        return this;
    }

    public JsonPaymentPermission getJsonPaymentPermission() {
        return jsonPaymentPermission;
    }

    public JsonTopic setJsonPaymentPermission(JsonPaymentPermission jsonPaymentPermission) {
        this.jsonPaymentPermission = jsonPaymentPermission;
        return this;
    }
}
