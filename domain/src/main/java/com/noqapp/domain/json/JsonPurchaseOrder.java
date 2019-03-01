package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.json.payment.cashfree.JsonPurchaseToken;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * hitender
 * 3/31/18 12:00 PM
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
public class JsonPurchaseOrder extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonPurchaseOrder.class);

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty ("p")
    private String customerPhone;

    @JsonProperty ("da")
    private String deliveryAddress;

    @JsonProperty ("sd")
    private int storeDiscount;

    @JsonProperty ("op")
    private String orderPrice;

    @JsonProperty ("dm")
    private DeliveryModeEnum deliveryMode;

    @JsonProperty ("pm")
    private PaymentModeEnum paymentMode;

    @JsonProperty("py")
    private PaymentStatusEnum paymentStatus;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("pop")
    private List<JsonPurchaseOrderProduct> jsonPurchaseOrderProducts = new LinkedList<>();

    /* Populated from TokenQueue. */
    @JsonProperty ("s")
    private int servingNumber;

    @JsonProperty ("t")
    private int token;

    @JsonProperty ("n")
    private String customerName;

    @JsonProperty ("e")
    private String expectedServiceBegin;

    @JsonProperty ("ti")
    private String transactionId;

    @JsonProperty ("ps")
    private PurchaseOrderStateEnum presentOrderState;

    @JsonProperty ("c")
    private String created;

    @JsonProperty("an")
    private String additionalNote;

    @JsonProperty("purt")
    private JsonPurchaseToken jsonPurchaseToken;

    public JsonPurchaseOrder() {
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public JsonPurchaseOrder setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonPurchaseOrder setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonPurchaseOrder setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonPurchaseOrder setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public JsonPurchaseOrder setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public JsonPurchaseOrder setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public int getStoreDiscount() {
        return storeDiscount;
    }

    public JsonPurchaseOrder setStoreDiscount(int storeDiscount) {
        this.storeDiscount = storeDiscount;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public JsonPurchaseOrder setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public DeliveryModeEnum getDeliveryMode() {
        return deliveryMode;
    }

    public JsonPurchaseOrder setDeliveryMode(DeliveryModeEnum deliveryMode) {
        this.deliveryMode = deliveryMode;
        return this;
    }

    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public JsonPurchaseOrder setPaymentMode(PaymentModeEnum paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public JsonPurchaseOrder setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonPurchaseOrder setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public List<JsonPurchaseOrderProduct> getJsonPurchaseOrderProducts() {
        return jsonPurchaseOrderProducts;
    }

    public JsonPurchaseOrder setJsonPurchaseOrderProducts(List<JsonPurchaseOrderProduct> jsonPurchaseOrderProducts) {
        this.jsonPurchaseOrderProducts = jsonPurchaseOrderProducts;
        return this;
    }

    public JsonPurchaseOrder addJsonPurchaseOrderProduct(JsonPurchaseOrderProduct jsonPurchaseOrderProduct) {
        this.jsonPurchaseOrderProducts.add(jsonPurchaseOrderProduct);
        return this;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public JsonPurchaseOrder setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public int getToken() {
        return token;
    }

    public JsonPurchaseOrder setToken(int token) {
        this.token = token;
        return this;
    }

    public String getExpectedServiceBegin() {
        return expectedServiceBegin;
    }

    public JsonPurchaseOrder setExpectedServiceBegin(String expectedServiceBegin) {
        this.expectedServiceBegin = expectedServiceBegin;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public JsonPurchaseOrder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public PurchaseOrderStateEnum getPresentOrderState() {
        return presentOrderState;
    }

    public JsonPurchaseOrder setPresentOrderState(PurchaseOrderStateEnum presentOrderState) {
        this.presentOrderState = presentOrderState;
        return this;
    }

    public String getCreated() {
        return created;
    }

    public JsonPurchaseOrder setCreated(String created) {
        this.created = created;
        return this;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public JsonPurchaseOrder setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
        return this;
    }

    public JsonPurchaseToken getJsonPurchaseToken() {
        return jsonPurchaseToken;
    }

    public JsonPurchaseOrder setJsonPurchaseToken(JsonPurchaseToken jsonPurchaseToken) {
        this.jsonPurchaseToken = jsonPurchaseToken;
        return this;
    }

    /** Mostly used when cancelling the order. */
    public static JsonPurchaseOrder populateForCancellingOrder(PurchaseOrderEntity po) {
        return new JsonPurchaseOrder()
            .setBizStoreId(po.getBizStoreId())
            .setCustomerPhone(po.getCustomerPhone())
            .setDeliveryAddress(po.getDeliveryAddress())
            .setStoreDiscount(po.getStoreDiscount())
            .setOrderPrice(po.getOrderPrice())
            .setDeliveryMode(po.getDeliveryMode())
            .setPaymentMode(po.getPaymentMode())
            .setPaymentStatus(po.getPaymentStatus())
            .setBusinessType(po.getBusinessType())
            //Empty purchaseOrderProducts List
            //No Setting Serving Number
            .setToken(po.getTokenNumber())
            .setCustomerName(po.getCustomerName())
            //No setting expectedServiceBegin
            .setTransactionId(po.getTransactionId())
            .setPresentOrderState(po.getPresentOrderState())
            .setCreated(DateFormatUtils.format(po.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
            .setAdditionalNote(po.getAdditionalNote());
    }

    public JsonPurchaseOrder(PurchaseOrderEntity purchaseOrder, List<PurchaseOrderProductEntity> purchaseOrderProducts) {
        this.bizStoreId = purchaseOrder.getBizStoreId();
        this.customerPhone = purchaseOrder.getCustomerPhone();
        this.deliveryAddress = purchaseOrder.getDeliveryAddress();
        this.storeDiscount = purchaseOrder.getStoreDiscount();
        this.orderPrice = purchaseOrder.getOrderPrice();
        this.deliveryMode = purchaseOrder.getDeliveryMode();
        this.paymentMode = purchaseOrder.getPaymentMode();
        this.paymentStatus = purchaseOrder.getPaymentStatus();
        this.businessType = purchaseOrder.getBusinessType();

        for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
            jsonPurchaseOrderProducts.add(JsonPurchaseOrderProduct.populate(purchaseOrderProduct));
        }

        this.presentOrderState = purchaseOrder.getPresentOrderState();
        this.created = DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.additionalNote = purchaseOrder.getAdditionalNote();
    }

    @Override
    public String toString() {
        return "JsonPurchaseOrderCF{" +
            "bizStoreId='" + bizStoreId + '\'' +
            ", customerPhone='" + customerPhone + '\'' +
            ", deliveryAddress='" + deliveryAddress + '\'' +
            ", storeDiscount=" + storeDiscount +
            ", orderPrice='" + orderPrice + '\'' +
            ", deliveryMode=" + deliveryMode +
            ", paymentMode=" + paymentMode +
            ", businessType=" + businessType +
            ", jsonPurchaseOrderProducts=" + jsonPurchaseOrderProducts +
            ", servingNumber=" + servingNumber +
            ", token=" + token +
            ", customerName='" + customerName + '\'' +
            ", expectedServiceBegin='" + expectedServiceBegin + '\'' +
            ", transactionId='" + transactionId + '\'' +
            ", presentOrderState=" + presentOrderState +
            ", created='" + created + '\'' +
            '}';
    }
}
