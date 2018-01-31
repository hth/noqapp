package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.*;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.QueueStatusEnum;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimeZone;

/**
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

    @JsonProperty ("c")
    private String codeQR;

    @JsonProperty ("d")
    private String displayName;

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

    @JsonProperty ("v")
    private boolean clientVisitedThisStore;

    JsonToken() {}

    public JsonToken(TokenQueueEntity tokenQueue) {
        this.codeQR = tokenQueue.getId();
        this.token = tokenQueue.getLastNumber();
        this.servingNumber = tokenQueue.getCurrentlyServing();
        this.displayName = tokenQueue.getDisplayName();
        this.queueStatus = tokenQueue.getQueueStatus();
    }

    public JsonToken(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
    }

    public JsonToken(String codeQR) {
        this.codeQR = codeQR;
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

    public boolean isClientVisitedThisStore() {
        return clientVisitedThisStore;
    }

    public JsonToken setClientVisitedThisStore(boolean clientVisitedThisStore) {
        this.clientVisitedThisStore = clientVisitedThisStore;
        return this;
    }

    @Override
    public String toString() {
        return "JsonToken{" +
                "codeQR='" + codeQR + '\'' +
                ", displayName='" + displayName + '\'' +
                ", queueStatus=" + queueStatus +
                ", servingNumber=" + servingNumber +
                ", token=" + token +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
