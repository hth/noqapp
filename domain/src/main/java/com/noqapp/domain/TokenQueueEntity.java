package com.noqapp.domain;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.QueueStatusEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.StringJoiner;

/**
 * Token should exists only when open for business or when token is suppose to be made available.
 * User: hitender
 * Date: 12/15/16 9:42 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "TOKEN_QUEUE")
public class TokenQueueEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(TokenQueueEntity.class);
    private static final String TOPICS = "/topics/";

    @Field("LN")
    private int lastNumber;

    @Field("CS")
    private int currentlyServing;

    @Field("TP")
    private String topic;

    @Field("DN")
    private String displayName;

    @Field("BT")
    private BusinessTypeEnum businessType;

    @Field("BC")
    private String bizCategoryId;

    @Field("QS")
    private QueueStatusEnum queueStatus = QueueStatusEnum.S;

    @Field("AP")
    private String appendPrefix = Constants.appendPrefix;

    @Transient
    private FirebaseMessageTypeEnum firebaseMessageType;

    public TokenQueueEntity() {
        //Required default constructor
    }

    public TokenQueueEntity(String topic, String displayName) {
        this.topic = topic;
        this.displayName = displayName;
    }

    public TokenQueueEntity(String topic, String displayName, BusinessTypeEnum businessType, String bizCategoryId) {
        this.topic = topic;
        this.displayName = displayName;
        this.businessType = businessType;
        this.bizCategoryId = bizCategoryId;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public TokenQueueEntity setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public int getCurrentlyServing() {
        return currentlyServing;
    }

    public TokenQueueEntity setCurrentlyServing(int currentlyServing) {
        this.currentlyServing = currentlyServing;
        return this;
    }

    public void closeQueue() {
        this.queueStatus = QueueStatusEnum.C;
    }

    public String getTopic() {
        return topic;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public TokenQueueEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public TokenQueueEntity setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public TokenQueueEntity setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getAppendPrefix() {
        return appendPrefix;
    }

    public TokenQueueEntity setAppendPrefix(String appendPrefix) {
        this.appendPrefix = appendPrefix;
        return this;
    }

    @Transient
    private String getTopicWellFormatted() {
        return TOPICS + topic;
    }

    @Transient
    private String getMerchantTopicWellFormatted() {
        return TOPICS + topic + "_M";
    }

    @Transient
    public String getCorrectTopic(QueueStatusEnum queueStatus) {
        switch (queueStatus) {
            case S:
            case R:
            case D:
                firebaseMessageType = FirebaseMessageTypeEnum.M;
                return getMerchantTopicWellFormatted();
            case N:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case C:
                //Very specific message to send to all on queue closed
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            default:
                LOG.error("Reached unreachable condition, queueStatus={}", queueStatus);
                throw new IllegalStateException("Condition set is not defined");
        }
    }

    @Transient
    public String getCorrectTopic(PurchaseOrderStateEnum purchaseOrderState) {
        switch (purchaseOrderState) {
            case IN:
            case PC:
            case VB:
            case IB:
            case FO:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case PO:
            case NM:
                firebaseMessageType = FirebaseMessageTypeEnum.M;
                return getMerchantTopicWellFormatted();
            case OP:
            case PR:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case RP:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case RD:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case OW:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case LO:
            case FD:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case OD:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case DA:
                firebaseMessageType = FirebaseMessageTypeEnum.C;
                return getTopicWellFormatted();
            case CO:
                firebaseMessageType = FirebaseMessageTypeEnum.M;
                return getMerchantTopicWellFormatted();
            default:
                LOG.error("Reached unreachable condition, queueStatus={}", queueStatus);
                throw new IllegalStateException("Condition set is not defined");
        }
    }

    @Transient
    public FirebaseMessageTypeEnum getFirebaseMessageType() {
        return firebaseMessageType;
    }

    @Transient
    public String totalWaiting() {
        int waiting = numberOfPeopleInQueue();
        if (waiting > 0) {
            return String.valueOf(waiting);
        } else {
            return "no";
        }
    }

    @Transient
    public int numberOfPeopleInQueue() {
        return lastNumber - currentlyServing;
    }

    @Transient
    public String generateDisplayToken() {
        if (StringUtils.isBlank(appendPrefix)) {
            return String.valueOf(100 + lastNumber);
        } else {
            return appendPrefix + (100 + lastNumber);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TokenQueueEntity.class.getSimpleName() + "[", "]")
            .add("lastNumber=" + lastNumber)
            .add("currentlyServing=" + currentlyServing)
            .add("topic='" + topic + "'")
            .add("displayName='" + displayName + "'")
            .add("businessType=" + businessType)
            .add("bizCategoryId='" + bizCategoryId + "'")
            .add("queueStatus=" + queueStatus)
            .add("firebaseMessageType=" + firebaseMessageType)
            .toString();
    }
}
