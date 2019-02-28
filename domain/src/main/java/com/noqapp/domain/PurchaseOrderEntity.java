package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 3/29/18 3:36 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "PURCHASE_ORDER")
@CompoundIndexes(value = {
        @CompoundIndex(name = "po_qid_bs_idx", def = "{'QID' : 1, 'BS' : 1}", unique = false),
        @CompoundIndex(name = "po_bn_idx", def = "{'BN' : 1}", unique = false),
        @CompoundIndex(name = "po_qr_idx", def = "{'QR' : 1}", unique = false),
        @CompoundIndex(name = "po_ti_idx", def = "{'TI' : 1}", unique = true),
})
public class PurchaseOrderEntity extends BaseEntity {

    @Field("QID")
    private String queueUserId;

    @Field("BS")
    private String bizStoreId;

    @Field("BN")
    private String bizNameId;

    @Field("QR")
    private String codeQR;

    @Field ("DID")
    private String did;

    @Field("CN")
    private String customerName;

    @Field("DA")
    private String deliveryAddress;

    @Field("CP")
    private String customerPhone;

    @Field("SD")
    private int storeDiscount;

    @Field("OP")
    private String orderPrice;

    @Field("DM")
    private DeliveryModeEnum deliveryMode;

    @Field("PM")
    private PaymentModeEnum paymentMode;

    @Field("PY")
    private PaymentStatusEnum paymentStatus = PaymentStatusEnum.UP;

    @Field("PS")
    private PurchaseOrderStateEnum presentOrderState = PurchaseOrderStateEnum.IN;

    @Field ("NS")
    private boolean notifiedOnService = false;

    @Field ("NC")
    private int attemptToSendNotificationCounts = 0;

    @Field("OS")
    private List<PurchaseOrderStateEnum> orderStates = new LinkedList<PurchaseOrderStateEnum>() {{add(PurchaseOrderStateEnum.IN);}};

    @Field ("BT")
    private BusinessTypeEnum businessType;

    @Field ("RA")
    private int ratingCount;

    @Field ("RV")
    private String review;

    /* Order Number. */
    @Field ("TN")
    private int tokenNumber;

    /* Locked when being served. */
    @Field ("SN")
    private String serverName;

    @Field ("SID")
    private String serverDeviceId;

    @Field ("SB")
    private Date serviceBeginTime;

    @Field ("SE")
    private Date serviceEndTime;

    @Field ("EB")
    private Date expectedServiceBegin;

    @Field ("TS")
    private TokenServiceEnum tokenService;

    @Field ("VS")
    private boolean clientVisitedThisStore;

    @Field ("TI")
    private String transactionId;

    @Field("DN")
    private String displayName;

    @Field("AN")
    private String additionalNote;

    @SuppressWarnings("unused")
    public PurchaseOrderEntity() {
        //Default constructor, required to keep bean happy
    }

    public PurchaseOrderEntity(String queueUserId, String bizStoreId, String bizNameId, String codeQR) {
        this.queueUserId = queueUserId;
        this.bizStoreId = bizStoreId;
        this.bizNameId = bizNameId;
        this.codeQR = codeQR;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public String getDid() {
        return did;
    }

    public PurchaseOrderEntity setDid(String did) {
        this.did = did;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public PurchaseOrderEntity setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public PurchaseOrderEntity setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public PurchaseOrderEntity setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public int getStoreDiscount() {
        return storeDiscount;
    }

    public PurchaseOrderEntity setStoreDiscount(int storeDiscount) {
        this.storeDiscount = storeDiscount;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public PurchaseOrderEntity setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public DeliveryModeEnum getDeliveryMode() {
        return deliveryMode;
    }

    public PurchaseOrderEntity setDeliveryMode(DeliveryModeEnum deliveryMode) {
        this.deliveryMode = deliveryMode;
        return this;
    }

    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public PurchaseOrderEntity setPaymentMode(PaymentModeEnum paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public PurchaseOrderEntity setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public PurchaseOrderStateEnum getPresentOrderState() {
        return presentOrderState;
    }

    public PurchaseOrderEntity setPresentOrderState() {
        this.presentOrderState = this.orderStates.get(orderStates.size() - 1);
        return this;
    }

    public boolean isNotifiedOnService() {
        return notifiedOnService;
    }

    public PurchaseOrderEntity setNotifiedOnService(boolean notifiedOnService) {
        this.notifiedOnService = notifiedOnService;
        return this;
    }

    public int getAttemptToSendNotificationCounts() {
        return attemptToSendNotificationCounts;
    }

    public PurchaseOrderEntity setAttemptToSendNotificationCounts(int attemptToSendNotificationCounts) {
        this.attemptToSendNotificationCounts = attemptToSendNotificationCounts;
        return this;
    }

    public List<PurchaseOrderStateEnum> getOrderStates() {
        return orderStates;
    }

    public PurchaseOrderEntity addOrderState(PurchaseOrderStateEnum orderState) {
        this.orderStates.add(orderState);
        this.presentOrderState = orderState;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public PurchaseOrderEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public PurchaseOrderEntity setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public String getReview() {
        return review;
    }

    public PurchaseOrderEntity setReview(String review) {
        this.review = review;
        return this;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public PurchaseOrderEntity setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public PurchaseOrderEntity setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getServerDeviceId() {
        return serverDeviceId;
    }

    public PurchaseOrderEntity setServerDeviceId(String serverDeviceId) {
        this.serverDeviceId = serverDeviceId;
        return this;
    }

    public Date getServiceBeginTime() {
        return serviceBeginTime;
    }

    public PurchaseOrderEntity setServiceBeginTime(Date serviceBeginTime) {
        this.serviceBeginTime = serviceBeginTime;
        return this;
    }

    public Date getServiceEndTime() {
        return serviceEndTime;
    }

    public PurchaseOrderEntity setServiceEndTime(Date serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
        return this;
    }

    public Date getExpectedServiceBegin() {
        return expectedServiceBegin;
    }

    public PurchaseOrderEntity setExpectedServiceBegin(Date expectedServiceBegin) {
        this.expectedServiceBegin = expectedServiceBegin;
        return this;
    }

    public TokenServiceEnum getTokenService() {
        return tokenService;
    }

    public PurchaseOrderEntity setTokenService(TokenServiceEnum tokenService) {
        this.tokenService = tokenService;
        return this;
    }

    public boolean isClientVisitedThisStore() {
        return clientVisitedThisStore;
    }

    public PurchaseOrderEntity setClientVisitedThisStore(boolean clientVisitedThisStore) {
        this.clientVisitedThisStore = clientVisitedThisStore;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PurchaseOrderEntity setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PurchaseOrderEntity setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public PurchaseOrderEntity setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
        return this;
    }

    @Override
    public String toString() {
        return "PurchaseOrderEntity{" +
            "queueUserId='" + queueUserId + '\'' +
            ", bizStoreId='" + bizStoreId + '\'' +
            ", bizNameId='" + bizNameId + '\'' +
            ", codeQR='" + codeQR + '\'' +
            ", did='" + did + '\'' +
            ", customerName='" + customerName + '\'' +
            ", deliveryAddress='" + deliveryAddress + '\'' +
            ", customerPhone='" + customerPhone + '\'' +
            ", storeDiscount=" + storeDiscount +
            ", orderPrice='" + orderPrice + '\'' +
            ", deliveryMode=" + deliveryMode +
            ", paymentMode=" + paymentMode +
            ", presentOrderState=" + presentOrderState +
            ", notifiedOnService=" + notifiedOnService +
            ", attemptToSendNotificationCounts=" + attemptToSendNotificationCounts +
            ", orderStates=" + orderStates +
            ", businessType=" + businessType +
            ", ratingCount=" + ratingCount +
            ", review='" + review + '\'' +
            ", tokenNumber=" + tokenNumber +
            ", serverName='" + serverName + '\'' +
            ", serverDeviceId='" + serverDeviceId + '\'' +
            ", serviceBeginTime=" + serviceBeginTime +
            ", serviceEndTime=" + serviceEndTime +
            ", expectedServiceBegin=" + expectedServiceBegin +
            ", tokenService=" + tokenService +
            ", clientVisitedThisStore=" + clientVisitedThisStore +
            ", transactionId='" + transactionId + '\'' +
            ", displayName='" + displayName + '\'' +
            '}';
    }
}
