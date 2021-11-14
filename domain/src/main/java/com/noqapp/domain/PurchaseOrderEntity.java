package com.noqapp.domain;

import com.noqapp.common.utils.MathUtil;
import com.noqapp.domain.annotation.DBMapping;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.TransactionViaEnum;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 3/29/18 3:36 AM
 */
@SuppressWarnings({
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

    @DBMapping
    @Field("QID")
    private String queueUserId;

    @DBMapping
    @Field("BS")
    private String bizStoreId;

    @DBMapping
    @Field("BN")
    private String bizNameId;

    @DBMapping
    @Field("QR")
    private String codeQR;

    /**
     * Device ID of purchaser. DID is of the purchaserQid. Helps in notifying user of changes through FCM.
     * Or
     * Guardian's DID.
     */
    @DBMapping
    @Field("DID")
    private String did;

    @Field("CN")
    private String customerName;

    @DBMapping
    @Field("AI")
    private String userAddressId;

    @Field("CP")
    private String customerPhone;

    @DBMapping
    @Field("SD")
    private int storeDiscount;

    @DBMapping
    @Field("PP")
    private String partialPayment;

    @DBMapping
    @Field("OP")
    private String orderPrice;

    @DBMapping
    @Field("TA")
    private String tax;

    @DBMapping
    @Field("GT")
    private String grandTotal;

    @DBMapping
    @Field("DM")
    private DeliveryModeEnum deliveryMode;

    @DBMapping
    @Field("PM")
    private PaymentModeEnum paymentMode;

    @DBMapping
    @Field("PY")
    private PaymentStatusEnum paymentStatus = PaymentStatusEnum.PP;

    @DBMapping
    @Field("PS")
    private PurchaseOrderStateEnum presentOrderState = PurchaseOrderStateEnum.IN;

    @Field("NS")
    private boolean notifiedOnService = false;

    @Field("NC")
    private int attemptToSendNotificationCounts = 0;

    @Field("OS")
    private List<PurchaseOrderStateEnum> orderStates = new LinkedList<>() {{
        add(PurchaseOrderStateEnum.IN);
    }};

    @DBMapping
    @Field("BT")
    private BusinessTypeEnum businessType;

    @DBMapping
    @Field("PQ")
    private String partialPaymentAcceptedByQid;

    @DBMapping
    @Field("FQ")
    private String fullPaymentAcceptedByQid;

    @DBMapping
    @Field("CQ")
    private String couponAddedByQid;

    @DBMapping
    @Field("CI")
    private String couponId;

    @Field("DP")
    private boolean discountedPurchase;

    @DBMapping
    @Field("RA")
    private int ratingCount;

    @DBMapping
    @Field("RV")
    private String review;

    @DBMapping
    @Field("ST")
    private SentimentTypeEnum sentimentType;

    /**
     * Order Number.
     * TODO(hth) There is a possibility of having same token number in purchase order, please validate as this happened for Queue.
     * Do no rely on tokenNumber when dealing with transactions. Instead use transaction id for all transaction and qid.
     */
    @DBMapping
    @Field("TN")
    private int tokenNumber;

    @Field("DT")
    private String displayToken;

    /* Locked when being served. */
    @DBMapping
    @Field("SN")
    private String serverName;

    @Field("SID")
    private String serverDeviceId;

    @DBMapping
    @Field("SB")
    private Date serviceBeginTime;

    @DBMapping
    @Field("SE")
    private Date serviceEndTime;

    @Field("EB")
    private Date expectedServiceBegin;

    @Field("TS")
    private TokenServiceEnum tokenService;

    @Field("VS")
    private boolean clientVisitedThisStore;

    @Field("VSD")
    private Date clientVisitedThisStoreDate;

    @DBMapping
    @Field("TI")
    private String transactionId;

    @DBMapping
    @Field("DN")
    private String displayName;

    @DBMapping
    @Field("AN")
    private String additionalNote;

    @DBMapping
    @Field("TM")
    private String transactionMessage;

    @DBMapping
    @Field("TR")
    private String transactionReferenceId;

    @DBMapping
    @Field("TV")
    private TransactionViaEnum transactionVia;

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

    public String getUserAddressId() {
        return userAddressId;
    }

    public PurchaseOrderEntity setUserAddressId(String userAddressId) {
        this.userAddressId = userAddressId;
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

    public String getPartialPayment() {
        return partialPayment;
    }

    public PurchaseOrderEntity setPartialPayment(String partialPayment) {
        this.partialPayment = partialPayment;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public PurchaseOrderEntity setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public String getTax() {
        return tax;
    }

    public PurchaseOrderEntity setTax(String tax) {
        this.tax = tax;
        return this;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public PurchaseOrderEntity setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
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

    public String getPartialPaymentAcceptedByQid() {
        return partialPaymentAcceptedByQid;
    }

    public PurchaseOrderEntity setPartialPaymentAcceptedByQid(String partialPaymentAcceptedByQid) {
        this.partialPaymentAcceptedByQid = partialPaymentAcceptedByQid;
        return this;
    }

    public String getFullPaymentAcceptedByQid() {
        return fullPaymentAcceptedByQid;
    }

    public PurchaseOrderEntity setFullPaymentAcceptedByQid(String fullPaymentAcceptedByQid) {
        this.fullPaymentAcceptedByQid = fullPaymentAcceptedByQid;
        return this;
    }

    public String getCouponAddedByQid() {
        return couponAddedByQid;
    }

    public PurchaseOrderEntity setCouponAddedByQid(String couponAddedByQid) {
        this.couponAddedByQid = couponAddedByQid;
        return this;
    }

    public String getCouponId() {
        return couponId;
    }

    public PurchaseOrderEntity setCouponId(String couponId) {
        this.couponId = couponId;
        return this;
    }

    public boolean isDiscountedPurchase() {
        return discountedPurchase;
    }

    public PurchaseOrderEntity setDiscountedPurchase(boolean discountedPurchase) {
        this.discountedPurchase = discountedPurchase;
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

    public SentimentTypeEnum getSentimentType() {
        return sentimentType;
    }

    public PurchaseOrderEntity setSentimentType(SentimentTypeEnum sentimentType) {
        this.sentimentType = sentimentType;
        return this;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public PurchaseOrderEntity setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
        return this;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public PurchaseOrderEntity setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
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

    public Date getClientVisitedThisStoreDate() {
        return clientVisitedThisStoreDate;
    }

    public PurchaseOrderEntity setClientVisitedThisStoreDate(Date clientVisitedThisStoreDate) {
        this.clientVisitedThisStoreDate = clientVisitedThisStoreDate;
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

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public PurchaseOrderEntity setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
        return this;
    }

    public String getTransactionReferenceId() {
        return transactionReferenceId;
    }

    public PurchaseOrderEntity setTransactionReferenceId(String transactionReferenceId) {
        this.transactionReferenceId = transactionReferenceId;
        return this;
    }

    public TransactionViaEnum getTransactionVia() {
        return transactionVia;
    }

    public PurchaseOrderEntity setTransactionVia(TransactionViaEnum transactionVia) {
        this.transactionVia = transactionVia;
        return this;
    }

    /** Shifting decimal point. */
    @Transient
    public String orderPriceForTransaction() {
        BigDecimal transactionAmount = new BigDecimal(grandTotal);
        if (StringUtils.isNotBlank(partialPayment)) {
            transactionAmount = transactionAmount.subtract(new BigDecimal(partialPayment));
        }
        return correctPriceForTransaction(transactionAmount);
    }

    private static String correctPriceForTransaction(final BigDecimal transactionAmount) {
        return transactionAmount.scaleByPowerOfTen(-2).toString();
    }

    public static String correctPriceForTransaction(final String transactionGrandTotal) {
        return correctPriceForTransaction(new BigDecimal(transactionGrandTotal));
    }

    @Transient
    public String getOrderPriceForDisplay() {
        return MathUtil.displayPrice(orderPrice);
    }

    @Transient
    public String getGrandTotalForDisplay() {
        return MathUtil.displayPrice(grandTotal);
    }

    @Transient
    public String getPartialPaymentForDisplay() {
        if (StringUtils.isNotBlank(partialPayment)) {
            return MathUtil.displayPrice(partialPayment);
        }

        return "";
    }

    @Transient
    public int total() {
        return Integer.parseInt(orderPrice) + Integer.parseInt(StringUtils.isBlank(tax) ? "0" : tax);
    }

    @Transient
    public boolean isTransactionNotSupported() {
        BigDecimal computedGrandTotal = new BigDecimal(grandTotal).movePointLeft(2);
        if (computedGrandTotal.compareTo(BigDecimal.ZERO) == 0) {
            //Ignore transaction with zero value
            return false;
        }

        //Do not support transaction less than 1
        return computedGrandTotal.compareTo(BigDecimal.ONE) < 0;
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
            ", userAddressId='" + userAddressId + '\'' +
            ", customerPhone='" + customerPhone + '\'' +
            ", storeDiscount=" + storeDiscount +
            ", partialPayment='" + partialPayment + '\'' +
            ", orderPrice='" + orderPrice + '\'' +
            ", deliveryMode=" + deliveryMode +
            ", paymentMode=" + paymentMode +
            ", paymentStatus=" + paymentStatus +
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
            ", additionalNote='" + additionalNote + '\'' +
            ", transactionMessage='" + transactionMessage + '\'' +
            ", transactionReferenceId='" + transactionReferenceId + '\'' +
            ", transactionVia=" + transactionVia +
            '}';
    }
}
