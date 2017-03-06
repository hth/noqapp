package com.token.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.token.domain.types.QueueStatusEnum;

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

    public TokenQueueEntity(String topic, String displayName) {
        this.topic = topic;
        this.displayName = displayName;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
    }

    public int getCurrentlyServing() {
        return currentlyServing;
    }

    public void setCurrentlyServing(int currentlyServing) {
        this.currentlyServing = currentlyServing;
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

    public void setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
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
                return getMerchantTopicWellFormatted();
            case N:
                 return getTopicWellFormatted();
            case C:
                //Very specific message to send to all on queue closed
                return  getTopicWellFormatted();
            default:
                LOG.error("Reached unreachable condition");
                throw new IllegalStateException("Condition set is not defined");
        }
    }

    @Transient
    public String totalWaiting() {
        int waiting = lastNumber - currentlyServing;
        if (waiting > 0) {
            return String.valueOf(waiting);
        } else {
            return "no";
        }
    }
}
