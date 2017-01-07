package com.token.domain.json.fcm;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.token.domain.AbstractDomain;
import com.token.domain.json.fcm.data.JsonNotification;
import com.token.domain.json.fcm.data.JsonTopicData;

/**
 * User: hitender
 * Date: 1/1/17 7:04 AM
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
public class JsonMessage extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMessage.class);

    @JsonProperty ("to")
    private String to;

    @JsonProperty ("priority")
    private String priority = "high";

    @JsonProperty ("content_available")
    private boolean content_available = true;

    @JsonProperty ("data")
    private JsonTopicData topicData;

    @JsonProperty ("notification")
    private JsonNotification notification;

    /**
     *
     * @param to        topic
     */
    public JsonMessage(String to) {
        this.to = to;
        this.topicData = new JsonTopicData();
        this.notification = new JsonNotification();
    }

    public String getTo() {
        return to;
    }

    public JsonTopicData getTopicData() {
        return topicData;
    }

    public JsonNotification getNotification() {
        return notification;
    }
}
