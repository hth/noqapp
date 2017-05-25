package com.noqapp.domain.json.fcm;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noqapp.domain.AbstractDomain;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonNotification;

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

    /* Can be topic or token. */
    @JsonProperty ("to")
    private String to;

    @JsonProperty ("priority")
    private String priority = "high";

    @JsonProperty ("content_available")
    private boolean content_available = true;

    @JsonProperty ("data")
    private JsonData data;

    /**
     * @param to topic or token
     */
    public JsonMessage(String to) {
        this.to = to;
        //this.notification = new JsonNotification();
    }

    public String getTo() {
        return to;
    }

    public JsonData getData() {
        return data;
    }

    public void setData(JsonData data) {
        this.data = data;
    }
}
