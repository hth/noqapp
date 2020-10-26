package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
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

    @JsonProperty ("ta")
    private String tax;

    @JsonProperty("dm")
    private DeliveryModeEnum deliveryMode;

    @JsonProperty("pm")
    private PaymentModeEnum paymentMode;

    @JsonProperty("py")
    private PaymentStatusEnum paymentStatus;

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

    @JsonProperty ("dt")
    private String displayToken;

    /* Locked when being served. */
    @JsonProperty("sn")
    private String serverName;

    @JsonProperty("sb")
    private String serviceBeginTime;

    @JsonProperty("se")
    private String serviceEndTime;

    @JsonProperty("ti")
    private String transactionId;

    /* This is business name. */
    @JsonProperty("dn")
    private String displayName;

    @JsonProperty("u")
    private String created;

    @JsonProperty("an")
    private String additionalNote;

    @JsonProperty("pops")
    private List<JsonPurchaseOrderProductHistorical> jsonPurchaseOrderProductHistoricalList = new ArrayList<>();

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    public JsonPurchaseOrderHistorical() {
        //Required default constructor
    }

    public JsonPurchaseOrderHistorical(PurchaseOrderEntity purchaseOrder, List<PurchaseOrderProductEntity> purchaseOrderProducts, BizStoreEntity bizStore) {
        this.queueUserId = purchaseOrder.getQueueUserId();
        this.codeQR = purchaseOrder.getCodeQR();
        this.deliveryAddress = purchaseOrder.getDeliveryAddress();
        this.storeDiscount = purchaseOrder.getStoreDiscount();
        this.orderPrice = purchaseOrder.getOrderPrice();
        this.tax = purchaseOrder.getTax();
        this.deliveryMode = purchaseOrder.getDeliveryMode();
        this.paymentMode = purchaseOrder.getPaymentMode();
        this.paymentStatus = purchaseOrder.getPaymentStatus();
        this.presentOrderState = purchaseOrder.getPresentOrderState();
        this.businessType = purchaseOrder.getBusinessType();
        this.ratingCount = purchaseOrder.getRatingCount();
        this.review = purchaseOrder.getReview();
        this.tokenNumber = purchaseOrder.getTokenNumber();
        this.displayToken = purchaseOrder.getDisplayToken();
        this.serverName = purchaseOrder.getServerName();
        this.serviceBeginTime = purchaseOrder.getServiceBeginTime() == null ? "" : DateFormatUtils.format(purchaseOrder.getServiceBeginTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.serviceEndTime = purchaseOrder.getServiceEndTime() == null ? "" : DateFormatUtils.format(purchaseOrder.getServiceEndTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.transactionId = purchaseOrder.getTransactionId();
        this.displayName = purchaseOrder.getDisplayName();
        this.created = DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.additionalNote = purchaseOrder.getAdditionalNote();

        for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
            jsonPurchaseOrderProductHistoricalList.add(new JsonPurchaseOrderProductHistorical(purchaseOrderProduct));
        }

        this.storeAddress = bizStore.getAddress();
        this.area = bizStore.getArea();
        this.town = bizStore.getTown();
        this.countryShortName = bizStore.getCountryShortName();
    }


    public String getQueueUserId() {
        return queueUserId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
