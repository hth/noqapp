package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimeZone;

/**
 * Since the object is broadcast, avoid using customer identifying details in here.
 *
 * User: hitender
 * Date: 11/18/16 11:56 AM
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
public class JsonToken extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonToken.class);

    @JsonProperty ("qr")
    private String codeQR;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty ("s")
    private int servingNumber;

    @JsonProperty ("t")
    private int token;

    @JsonProperty ("n")
    private String customerName = "";

    @JsonProperty ("e")
    private String expectedServiceBegin;

    /* Note: Avoid firebase broadcasting QID in JsonToken. Hence refrained from using QID here. */
    JsonToken() {}

    public JsonToken(TokenQueueEntity tokenQueue) {
        this.codeQR = tokenQueue.getId();
        this.token = tokenQueue.getLastNumber();
        this.servingNumber = tokenQueue.getCurrentlyServing();
        this.displayName = tokenQueue.getDisplayName();
        this.queueStatus = tokenQueue.getQueueStatus();
        this.businessType = tokenQueue.getBusinessType();
    }

    public JsonToken(String codeQR, BusinessTypeEnum businessType) {
        this.codeQR = codeQR;
        this.businessType = businessType;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonToken setToken(int token) {
        this.token = token;
        return this;
    }

    public int getToken() {
        return token;
    }

    public JsonToken setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public JsonToken setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonToken setQueueStatus(boolean closeQueue) {
        if (closeQueue) {
            queueStatus = QueueStatusEnum.C;
        } else {
            queueStatus = QueueStatusEnum.N;
        }
        return this;
    }

    public JsonToken setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonToken setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getExpectedServiceBegin() {
        return expectedServiceBegin;
    }

    public JsonToken setExpectedServiceBegin(Date expectedServiceBegin) {
        if (null != expectedServiceBegin) {
            this.expectedServiceBegin = DateFormatUtils.format(expectedServiceBegin, ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        }
        return this;
    }

    public JsonToken setExpectedServiceBegin(Date expectedServiceBegin, String timeZone) {
        if (null != expectedServiceBegin) {
            this.expectedServiceBegin = DateFormatUtils.format(expectedServiceBegin, ISO8601_FMT, TimeZone.getTimeZone(timeZone));
        }
        return this;
    }

    @Override
    public String toString() {
        return "JsonToken{" +
            "codeQR='" + codeQR + '\'' +
            ", displayName='" + displayName + '\'' +
            ", businessType=" + businessType +
            ", queueStatus=" + queueStatus +
            ", servingNumber=" + servingNumber +
            ", token=" + token +
            ", customerName='" + customerName + '\'' +
            ", expectedServiceBegin='" + expectedServiceBegin + '\'' +
            '}';
    }
}
