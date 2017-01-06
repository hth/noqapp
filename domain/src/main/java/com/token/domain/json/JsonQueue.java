package com.token.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: hitender
 * Date: 12/1/16 9:28 AM
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
public class JsonQueue {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueue.class);

    @JsonProperty ("c")
    private String codeQR;

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("a")
    private String storeAddress;

    @JsonProperty ("p")
    private String storePhone;

    @JsonProperty ("f")
    private int tokenAvailableFrom;

    /* Store business start hour. */
    @JsonProperty ("b")
    private int startHour;

    /* Store business end hour. */
    @JsonProperty ("e")
    private int endHour;

    @JsonProperty ("t")
    private String topic;

    @JsonProperty ("s")
    private int servingNumber;

    @JsonProperty ("l")
    private int lastNumber;

    @JsonProperty ("q")
    private boolean closeQueue;

    public JsonQueue(String codeQR) {
        this.codeQR = codeQR;
    }

    public JsonQueue setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public JsonQueue setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public JsonQueue setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public JsonQueue setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public JsonQueue setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public JsonQueue setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public JsonQueue setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public JsonQueue setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public JsonQueue setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public JsonQueue setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public JsonQueue setCloseQueue(boolean closeQueue) {
        this.closeQueue = closeQueue;
        return this;
    }
}
