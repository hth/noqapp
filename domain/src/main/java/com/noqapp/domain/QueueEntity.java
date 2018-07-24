package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 12/16/16 12:42 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "QUEUE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "queue_idx", def = "{'QR' : -1, 'DID': -1, 'QID': -1, 'GQ' : -1}", unique = false, background = true, sparse = true),
        @CompoundIndex (name = "queue_tn_idx", def = "{'QR' : -1, 'TN': -1}", unique = true, background = true)
})
public class QueueEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(QueueEntity.class);

    @NotNull
    @Field ("QR")
    private String codeQR;

    @Field ("DID")
    private String did;

    @Field ("QID")
    private String queueUserId;

    @NotNull
    @Field ("TN")
    private int tokenNumber;

    @NotNull
    @Field ("DN")
    private String displayName;

    @Field ("BT")
    private BusinessTypeEnum businessType;

    @NotNull
    @Field ("QS")
    private QueueUserStateEnum queueUserState = QueueUserStateEnum.Q;

    @NotNull
    @Field ("NS")
    private boolean notifiedOnService = false;

    @NotNull
    @Field ("NC")
    private int attemptToSendNotificationCounts = 0;

    @NotNull
    @Field ("CN")
    private String customerName;

    @Field ("PH")
    private String customerPhone;

    @Field ("RA")
    private int ratingCount;

    @Field ("HR")
    private int hoursSaved;

    @Field ("RV")
    private String review;

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

    @Field ("GQ")
    private String guardianQid;

    @Field ("BC")
    private String businessCustomerId;

    /* This data is associated to record that needs to be created against this queue entry. */
    @Field ("RR")
    private String recordReferenceId;

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
            String displayName,
            BusinessTypeEnum businessType
    ) {
        this.codeQR = codeQR;
        this.did = did;
        this.tokenService = tokenService;
        this.queueUserId = queueUserId;
        this.tokenNumber = tokenNumber;
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

    public String getDisplayName() {
        return displayName;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public void setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
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

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getHoursSaved() {
        return hoursSaved;
    }

    public void setHoursSaved(int hoursSaved) {
        this.hoursSaved = hoursSaved;
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
            case Q:
                LOG.info("QueueUserStateEnum={} create date={}", queueUserState, getCreated());
                return getCreated();
            case A:
            case N:
            case S:
                return serviceEndTime;
            default:
                LOG.error("Reached un-supported condition QueueUserStateEnum={}", queueUserState);
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

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public QueueEntity setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
        return this;
    }

    @Override
    public String toString() {
        return "QueueEntity{" +
                "codeQR='" + codeQR + '\'' +
                ", did='" + did + '\'' +
                ", queueUserId='" + queueUserId + '\'' +
                ", tokenNumber=" + tokenNumber +
                ", displayName='" + displayName + '\'' +
                ", queueUserState=" + queueUserState +
                ", serverName=" + serverName +
                ", notifiedOnService=" + notifiedOnService +
                ", attemptToSendNotificationCounts=" + attemptToSendNotificationCounts +
                ", customerName='" + customerName + '\'' +
                ", ratingCount=" + ratingCount +
                ", hoursSaved=" + hoursSaved +
                ", serviceEndTime=" + serviceEndTime +
                '}';
    }
}
