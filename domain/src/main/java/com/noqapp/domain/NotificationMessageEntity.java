package com.noqapp.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 6/5/20 1:59 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "NOTIFICATION_MESSAGE")
public class NotificationMessageEntity extends BaseEntity {

    @Field("QID")
    private String queueUserId;

    @Field("TL")
    private String title;

    @Field("BD")
    private String body;

    @Field("MC")
    private int messageSendCount;

    public String getQueueUserId() {
        return queueUserId;
    }

    public NotificationMessageEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public NotificationMessageEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBody() {
        return body;
    }

    public NotificationMessageEntity setBody(String body) {
        this.body = body;
        return this;
    }

    public int getMessageSendCount() {
        return messageSendCount;
    }

    public NotificationMessageEntity setMessageSendCount(int messageSendCount) {
        this.messageSendCount = messageSendCount;
        return this;
    }
}
