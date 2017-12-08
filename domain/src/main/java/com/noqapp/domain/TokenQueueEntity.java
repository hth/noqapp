package com.noqapp.domain;

import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Token should exists only when open for business or when token is suppose to be made available.
 * User: hitender
 * Date: 12/15/16 9:42 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "TOKEN_QUEUE")
public class TokenQueueEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(TokenQueueEntity.class);
    private static final String TOPICS = "/topics/";

    @Field ("LN")
    private int lastNumber;

    @Field ("CS")
    private int currentlyServing;

    @Field ("TP")
    private String topic;

    @Field ("DN")
    private String displayName;

    @Field ("QS")
    private QueueStatusEnum queueStatus = QueueStatusEnum.S;

    @Transient
    private FirebaseMessageTypeEnum firebaseMessageType;

    private TokenQueueEntity() {
        //Required default constructor
    }

    public TokenQueueEntity(String topic, String displayName) {
        this.topic = topic;
        this.displayName = displayName;
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

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public TokenQueueEntity setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
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
                return  getTopicWellFormatted();
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
}
