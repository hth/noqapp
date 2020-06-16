package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.StringJoiner;
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

    @JsonProperty ("bc")
    private String bizCategoryId;

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

    @JsonProperty("ti")
    @JsonIgnore
    private String transactionId;

    @JsonProperty("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    @Transient
    private Date expectedServiceBeginDate;

    /* Note: Avoid firebase broadcasting QID in JsonToken. Hence refrained from using QID here. */
    JsonToken() {}

    public JsonToken(TokenQueueEntity tokenQueue) {
        this.codeQR = tokenQueue.getId();
        this.token = tokenQueue.getLastNumber();
        this.servingNumber = tokenQueue.getCurrentlyServing();
        this.displayName = tokenQueue.getDisplayName();
        this.queueStatus = tokenQueue.getQueueStatus();
        this.businessType = tokenQueue.getBusinessType();
        this.bizCategoryId = tokenQueue.getBizCategoryId();
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

    public String getBizCategoryId() {
        return bizCategoryId;
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
            this.expectedServiceBeginDate = expectedServiceBegin;
            this.expectedServiceBegin = DateFormatUtils.format(expectedServiceBegin, ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        }
        return this;
    }

    public JsonToken setExpectedServiceBegin(Date expectedServiceBegin, String timeZone) {
        if (null != expectedServiceBegin) {
            this.expectedServiceBeginDate = expectedServiceBegin;
            this.expectedServiceBegin = DateFormatUtils.format(expectedServiceBegin, ISO8601_FMT, TimeZone.getTimeZone(timeZone));
        }
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public JsonToken setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    public JsonToken setJsonPurchaseOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        return this;
    }

    public Date getExpectedServiceBeginDate() {
        return expectedServiceBeginDate;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonToken.class.getSimpleName() + "[", "]")
            .add("codeQR='" + codeQR + "'")
            .add("displayName='" + displayName + "'")
            .add("businessType=" + businessType)
            .add("bizCategoryId='" + bizCategoryId + "'")
            .add("queueStatus=" + queueStatus)
            .add("servingNumber=" + servingNumber)
            .add("token=" + token)
            .add("customerName='" + customerName + "'")
            .add("expectedServiceBegin='" + expectedServiceBegin + "'")
            .add("transactionId='" + transactionId + "'")
            .add("jsonPurchaseOrder=" + jsonPurchaseOrder)
            .toString();
    }
}
