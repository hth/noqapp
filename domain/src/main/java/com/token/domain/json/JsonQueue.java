package com.token.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.token.domain.AbstractDomain;
import com.token.domain.types.QueueStatusEnum;

import java.util.Date;
import java.util.TimeZone;

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
public class JsonQueue extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueue.class);

    @JsonProperty ("c")
    private String codeQR;

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("sa")
    private String storeAddress;

    @JsonProperty ("p")
    private String storePhone;

    @JsonProperty ("f")
    private int tokenAvailableFrom;

    /* Store business start hour. */
    @JsonProperty ("b")
    private int startHour;

    @JsonProperty ("m")
    private int tokenNotAvailableFrom;

    /* Store business end hour. */
    @JsonProperty ("e")
    private int endHour;

    @JsonProperty ("o")
    private String topic;

    @JsonProperty ("s")
    private int servingNumber;

    @JsonProperty ("l")
    private int lastNumber;

    @JsonProperty ("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty ("u")
    private String createDate;

    public JsonQueue(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonQueue setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonQueue setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonQueue setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonQueue setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public JsonQueue setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public JsonQueue setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public JsonQueue setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public JsonQueue setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public JsonQueue setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public JsonQueue setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public JsonQueue setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonQueue setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date date) {
        this.createDate = DateFormatUtils.format(date, ISO8601_FMT, TimeZone.getTimeZone("UTC"));
    }
}
