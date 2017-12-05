package com.noqapp.domain;

import com.noqapp.domain.types.QueueUserStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;

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
        @CompoundIndex (name = "queue_idx", def = "{'QR' : -1, 'DID': -1, 'QID': -1}", unique = false, background = true, sparse = true),
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
    private String phone;

    @Field ("RA")
    private int ratingCount;

    @Field ("HR")
    private int hoursSaved;

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

    public QueueEntity(String codeQR, String did, String queueUserId, int tokenNumber, String displayName) {
        this.codeQR = codeQR;
        this.did = did;
        this.queueUserId = queueUserId;
        this.tokenNumber = tokenNumber;
        this.displayName = displayName;
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

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public QueueEntity setPhone(String phone) {
        this.phone = phone;
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
