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

    @Field("IU")
    private String imageURL;

    @Field("MC")
    private int messageSendCount;

    @Field("VC")
    private int viewClientCount;

    @Field("VU")
    private int viewUnregisteredCount;

    @Field("VB")
    private int viewBusinessCount;

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

    public String getImageURL() {
        return imageURL;
    }

    public NotificationMessageEntity setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public int getMessageSendCount() {
        return messageSendCount;
    }

    public NotificationMessageEntity setMessageSendCount(int messageSendCount) {
        this.messageSendCount = messageSendCount;
        return this;
    }

    public int getViewClientCount() {
        return viewClientCount;
    }

    public NotificationMessageEntity setViewClientCount(int viewClientCount) {
        this.viewClientCount = viewClientCount;
        return this;
    }

    public int getViewUnregisteredCount() {
        return viewUnregisteredCount;
    }

    public NotificationMessageEntity setViewUnregisteredCount(int viewUnregisteredCount) {
        this.viewUnregisteredCount = viewUnregisteredCount;
        return this;
    }

    public int getViewBusinessCount() {
        return viewBusinessCount;
    }

    public NotificationMessageEntity setViewBusinessCount(int viewBusinessCount) {
        this.viewBusinessCount = viewBusinessCount;
        return this;
    }
}
