package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryTypeEnum;
import com.noqapp.domain.types.PaymentTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

/**
 * hitender
 * 10/5/18 9:40 AM
 */
@SuppressWarnings ({
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
public class JsonPurchaseOrderHistorical extends AbstractDomain {

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("da")
    private String deliveryAddress;

    @JsonProperty("sd")
    private int storeDiscount;

    @JsonProperty("op")
    private String orderPrice;

    @JsonProperty("dm")
    private DeliveryTypeEnum deliveryType;

    @JsonProperty("pt")
    private PaymentTypeEnum paymentType;

    @JsonProperty("ps")
    private PurchaseOrderStateEnum presentOrderState;

    @JsonProperty ("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("ra")
    private int ratingCount;

    @JsonProperty ("rv")
    private String review;

    /* Order Number. */
    @JsonProperty ("tn")
    private int tokenNumber;

    /* Locked when being served. */
    @JsonProperty ("sn")
    private String serverName;

    @JsonProperty ("sb")
    private Date serviceBeginTime;

    @JsonProperty ("se")
    private Date serviceEndTime;

    @JsonProperty ("ti")
    private String transactionId;

    @JsonProperty ("dn")
    private String displayName;

    public JsonPurchaseOrderHistorical(PurchaseOrderEntity purchaseOrder) {
        this.queueUserId = purchaseOrder.getQueueUserId();
        this.codeQR = purchaseOrder.getCodeQR();
        this.deliveryAddress = purchaseOrder.getDeliveryAddress();
        this.storeDiscount = purchaseOrder.getStoreDiscount();
        this.orderPrice = purchaseOrder.getOrderPrice();
        this.deliveryType = purchaseOrder.getDeliveryType();
        this.paymentType = purchaseOrder.getPaymentType();
        this.presentOrderState = purchaseOrder.getPresentOrderState();
        this.businessType = purchaseOrder.getBusinessType();
        this.ratingCount = purchaseOrder.getRatingCount();
        this.review = purchaseOrder.getReview();
        this.tokenNumber = purchaseOrder.getTokenNumber();
        this.serverName = purchaseOrder.getServerName();
        this.serviceBeginTime = purchaseOrder.getServiceBeginTime();
        this.serviceEndTime = purchaseOrder.getServiceEndTime();
        this.transactionId = purchaseOrder.getTransactionId();
        this.displayName = purchaseOrder.getDisplayName();
    }
}
