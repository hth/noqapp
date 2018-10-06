package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryTypeEnum;
import com.noqapp.domain.types.PaymentTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * hitender
 * 10/5/18 9:40 AM
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

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("ra")
    private int ratingCount;

    @JsonProperty("rv")
    private String review;

    /* Order Number. */
    @JsonProperty("tn")
    private int tokenNumber;

    /* Locked when being served. */
    @JsonProperty("sn")
    private String serverName;

    @JsonProperty("sb")
    private String serviceBeginTime;

    @JsonProperty("se")
    private String serviceEndTime;

    @JsonProperty("ti")
    private String transactionId;

    @JsonProperty("dn")
    private String displayName;

    @JsonProperty("u")
    private String created;

    @JsonProperty("pops")
    private List<JsonPurchaseOrderProductHistorical> jsonPurchaseOrderProductHistoricalList = new ArrayList<>();

    public JsonPurchaseOrderHistorical() {
        //Required default constructor
    }

    public JsonPurchaseOrderHistorical(PurchaseOrderEntity purchaseOrder, List<PurchaseOrderProductEntity> purchaseOrderProducts) {
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
        this.serviceBeginTime = purchaseOrder.getServiceBeginTime() == null ? "" : DateFormatUtils.format(purchaseOrder.getServiceBeginTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.serviceEndTime = purchaseOrder.getServiceEndTime() == null ? "" : DateFormatUtils.format(purchaseOrder.getServiceEndTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.transactionId = purchaseOrder.getTransactionId();
        this.displayName = purchaseOrder.getDisplayName();
        this.created = DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

        for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
            jsonPurchaseOrderProductHistoricalList.add(new JsonPurchaseOrderProductHistorical(purchaseOrderProduct));
        }
    }
}
