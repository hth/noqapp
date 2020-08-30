package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 9/27/18 3:33 PM
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
public class JsonClientOrderData extends JsonData {
    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty ("qid")
    private String queueUserId;

    @JsonProperty("on")
    private int orderNumber;

    @JsonProperty("dt")
    private String displayToken;

    @JsonProperty("ps")
    private PurchaseOrderStateEnum purchaseOrderState;

    @JsonProperty("o")
    private String topic;

    public JsonClientOrderData(FirebaseMessageTypeEnum firebaseMessageType, MessageOriginEnum messageOrigin) {
        super(firebaseMessageType);
        this.messageOrigin = messageOrigin;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonClientOrderData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonClientOrderData setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public JsonClientOrderData setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public JsonClientOrderData setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
        return this;
    }

    public PurchaseOrderStateEnum getPurchaseOrderState() {
        return purchaseOrderState;
    }

    public JsonClientOrderData setPurchaseOrderState(PurchaseOrderStateEnum purchaseOrderState) {
        this.purchaseOrderState = purchaseOrderState;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public JsonClientOrderData setTopic(String topic) {
        this.topic = topic;
        return this;
    }
}
