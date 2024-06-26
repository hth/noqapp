package com.noqapp.domain;

import com.noqapp.domain.types.BusinessCustomerAttributeEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 12/16/16 12:42 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "QUEUE")
@CompoundIndexes(value = {
    @CompoundIndex(name = "queue_idx", def = "{'QR' : -1, 'DID': -1, 'QID': -1, 'GQ' : -1}", unique = false, background = true, sparse = true),
    @CompoundIndex(name = "queue_tn_idx", def = "{'QR' : -1, 'TN': -1}", unique = true, background = true)
})
public class QueueEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(QueueEntity.class);

    @NotNull
    @Field("QR")
    private String codeQR;

    @Field("DID")
    private String did;

    @Field("QID")
    private String queueUserId;

    @Field("TN")
    private int tokenNumber;

    @Field("DT")
    private String displayToken;

    @NotNull
    @Field("DN")
    private String displayName;

    @Field("BT")
    private BusinessTypeEnum businessType;

    @Field("QS")
    private QueueUserStateEnum queueUserState = QueueUserStateEnum.Q;

    @Field("NS")
    private boolean notifiedOnService = false;

    @Field("NC")
    private int attemptToSendNotificationCounts = 0;

    @NotNull
    @Field("CN")
    private String customerName;

    @Field("PH")
    private String customerPhone;

    @Field("RA")
    private int ratingCount;

    @Field("HR")
    private int hoursSaved;

    @Field("RV")
    private String review;

    /* Locked when being served. */
    @Field("SN")
    private String serverName;

    @Field("SID")
    private String serverDeviceId;

    @Field("SB")
    private Date serviceBeginTime;

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

    @Field("BN")
    private String bizNameId;

    @Field("VB")
    private boolean clientVisitedThisBusiness;

    @Field("GQ")
    private String guardianQid;

    @Field("BC")
    private String businessCustomerId;

    /* This checks how many times the Business Customer Id has been changed. */
    @Field("CC")
    private int businessCustomerIdChangeCount;

    /* This data is associated to record that needs to be created against this queue entry. */
    @Field("RR")
    private String recordReferenceId;

    @Field("ST")
    private SentimentTypeEnum sentimentType;

    @Field("TI")
    private String transactionId;

    @Field("PL")
    private CustomerPriorityLevelEnum customerPriorityLevel;

    @Field("CA")
    private Set<BusinessCustomerAttributeEnum> businessCustomerAttributes;

    @Field("AC")
    private String authorizedCheckByQid;

    @Field("SL")
    private String timeSlotMessage;

    @Field("OSL")
    private String originalTimeSlotMessage;

    @SuppressWarnings("unused")
    public QueueEntity() {
        //Default constructor, required to keep bean happy
    }

    public QueueEntity(
        String codeQR,
        String did,
        TokenServiceEnum tokenService,
        String queueUserId,
        int tokenNumber,
        String displayToken,
        String displayName,
        BusinessTypeEnum businessType
    ) {
        this.codeQR = codeQR;
        this.did = did;
        this.tokenService = tokenService;
        this.queueUserId = queueUserId;
        this.tokenNumber = tokenNumber;
        this.displayToken = displayToken;
        this.displayName = displayName;
        this.businessType = businessType;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public String getDid() {
        return did;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public QueueEntity setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
        return this;
    }

    public boolean isNotifiedOnService() {
        return notifiedOnService;
    }

    public void setNotifiedOnService(boolean notifiedOnService) {
        this.notifiedOnService = notifiedOnService;
    }

    public int getAttemptToSendNotificationCounts() {
        return attemptToSendNotificationCounts;
    }

    public String getCustomerName() {
        return customerName;
    }

    public QueueEntity setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public QueueEntity setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public QueueEntity setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public int getHoursSaved() {
        return hoursSaved;
    }

    public QueueEntity setHoursSaved(int hoursSaved) {
        this.hoursSaved = hoursSaved;
        return this;
    }

    public String getReview() {
        return review;
    }

    public QueueEntity setReview(String review) {
        this.review = review;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerDeviceId() {
        return serverDeviceId;
    }

    public void setServerDeviceId(String serverDeviceId) {
        this.serverDeviceId = serverDeviceId;
    }

    public Date getServiceBeginTime() {
        return serviceBeginTime;
    }

    public void setServiceBeginTime(Date serviceBeginTime) {
        this.serviceBeginTime = serviceBeginTime;
    }

    public Date getServiceEndTime() {
        switch (queueUserState) {
            case I:
            case Q:
                LOG.info("QueueUserStateEnum=\"{}\" create date={} qid={}", queueUserState, getCreated(), queueUserId);
                return getCreated();
            case A:
            case N:
            case S:
                return serviceEndTime;
            default:
                LOG.error("Reached un-supported condition QueueUserStateEnum=\"{}\" qid={}", queueUserState, queueUserId);
                return getCreated();
        }
    }

    public void setServiceEndTime(Date serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public long timeTakenForServiceInMilliSeconds() {
        return serviceEndTime.toInstant().toEpochMilli() - serviceBeginTime.toInstant().toEpochMilli();
    }

    public Date getExpectedServiceBegin() {
        return expectedServiceBegin;
    }

    public QueueEntity setExpectedServiceBegin(Date expectedServiceBegin) {
        this.expectedServiceBegin = expectedServiceBegin;
        return this;
    }

    public TokenServiceEnum getTokenService() {
        return tokenService;
    }

    public QueueEntity setTokenService(TokenServiceEnum tokenService) {
        this.tokenService = tokenService;
        return this;
    }

    public boolean hasClientVisitedThisStore() {
        return clientVisitedThisStore;
    }

    public QueueEntity setClientVisitedThisStore(boolean clientVisitedThisStore) {
        this.clientVisitedThisStore = clientVisitedThisStore;
        return this;
    }

    public Date getClientVisitedThisStoreDate() {
        return clientVisitedThisStoreDate;
    }

    public QueueEntity setClientVisitedThisStoreDate(Date clientVisitedThisStoreDate) {
        this.clientVisitedThisStoreDate = clientVisitedThisStoreDate;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public QueueEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public boolean hasClientVisitedThisBusiness() {
        return clientVisitedThisBusiness;
    }

    public QueueEntity setClientVisitedThisBusiness(boolean clientVisitedThisBusiness) {
        this.clientVisitedThisBusiness = clientVisitedThisBusiness;
        return this;
    }

    public String getGuardianQid() {
        return guardianQid;
    }

    public QueueEntity setGuardianQid(String guardianQid) {
        this.guardianQid = guardianQid;
        return this;
    }

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public QueueEntity setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public int getBusinessCustomerIdChangeCount() {
        return businessCustomerIdChangeCount;
    }

    public QueueEntity setBusinessCustomerIdChangeCount(int businessCustomerIdChangeCount) {
        this.businessCustomerIdChangeCount = businessCustomerIdChangeCount;
        return this;
    }

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public QueueEntity setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
        return this;
    }

    public SentimentTypeEnum getSentimentType() {
        return sentimentType;
    }

    public QueueEntity setSentimentType(SentimentTypeEnum sentimentType) {
        this.sentimentType = sentimentType;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public QueueEntity setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public CustomerPriorityLevelEnum getCustomerPriorityLevel() {
        return customerPriorityLevel;
    }

    public QueueEntity setCustomerPriorityLevel(CustomerPriorityLevelEnum customerPriorityLevel) {
        this.customerPriorityLevel = customerPriorityLevel;
        return this;
    }

    public Set<BusinessCustomerAttributeEnum> getBusinessCustomerAttributes() {
        return businessCustomerAttributes;
    }

    public QueueEntity setBusinessCustomerAttributes(Set<BusinessCustomerAttributeEnum> businessCustomerAttributes) {
        this.businessCustomerAttributes = businessCustomerAttributes;
        return this;
    }

    public String getAuthorizedCheckByQid() {
        return authorizedCheckByQid;
    }

    public QueueEntity setAuthorizedCheckByQid(String authorizedCheckByQid) {
        this.authorizedCheckByQid = authorizedCheckByQid;
        return this;
    }

    public String getTimeSlotMessage() {
        return timeSlotMessage;
    }

    public QueueEntity setTimeSlotMessage(String timeSlotMessage) {
        this.timeSlotMessage = timeSlotMessage;
        return this;
    }

    public String getOriginalTimeSlotMessage() {
        return originalTimeSlotMessage;
    }

    public QueueEntity setOriginalTimeSlotMessage(String originalTimeSlotMessage) {
        this.originalTimeSlotMessage = originalTimeSlotMessage;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QueueEntity.class.getSimpleName() + "[", "]")
            .add("codeQR='" + codeQR + "'")
            .add("did='" + did + "'")
            .add("queueUserId='" + queueUserId + "'")
            .add("tokenNumber=" + tokenNumber)
            .add("displayToken='" + displayToken + "'")
            .add("displayName='" + displayName + "'")
            .add("businessType=" + businessType)
            .add("queueUserState=" + queueUserState)
            .add("notifiedOnService=" + notifiedOnService)
            .add("attemptToSendNotificationCounts=" + attemptToSendNotificationCounts)
            .add("customerName='" + customerName + "'")
            .add("customerPhone='" + customerPhone + "'")
            .add("ratingCount=" + ratingCount)
            .add("hoursSaved=" + hoursSaved)
            .add("review='" + review + "'")
            .add("serverName='" + serverName + "'")
            .add("serverDeviceId='" + serverDeviceId + "'")
            .add("serviceBeginTime=" + serviceBeginTime)
            .add("serviceEndTime=" + serviceEndTime)
            .add("expectedServiceBegin=" + expectedServiceBegin)
            .add("tokenService=" + tokenService)
            .add("clientVisitedThisStore=" + clientVisitedThisStore)
            .add("clientVisitedThisStoreDate=" + clientVisitedThisStoreDate)
            .add("bizNameId='" + bizNameId + "'")
            .add("clientVisitedThisBusiness=" + clientVisitedThisBusiness)
            .add("guardianQid='" + guardianQid + "'")
            .add("businessCustomerId='" + businessCustomerId + "'")
            .add("businessCustomerIdChangeCount=" + businessCustomerIdChangeCount)
            .add("recordReferenceId='" + recordReferenceId + "'")
            .add("sentimentType=" + sentimentType)
            .add("transactionId='" + transactionId + "'")
            .add("customerPriorityLevel=" + customerPriorityLevel)
            .add("businessCustomerAttributes=" + businessCustomerAttributes)
            .add("authorizedCheckByQid='" + authorizedCheckByQid + "'")
            .add("timeSlotMessage='" + timeSlotMessage + "'")
            .add("originalTimeSlotMessage='" + originalTimeSlotMessage + "'")
            .toString();
    }
}
