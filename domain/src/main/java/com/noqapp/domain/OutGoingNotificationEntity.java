package com.noqapp.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 12/9/20 5:32 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "OUT_GOING_NOTIFICATION")
public class OutGoingNotificationEntity extends BaseEntity {
    @Field("TL")
    private String title;

    @Field("BD")
    private String body;

    @Field("TO")
    private String topic;

    @Field("WY")
    private int weekYear;

    @Field("ST")
    private boolean sent;

    public String getTitle() {
        return title;
    }

    public OutGoingNotificationEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBody() {
        return body;
    }

    public OutGoingNotificationEntity setBody(String body) {
        this.body = body;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public OutGoingNotificationEntity setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getWeekYear() {
        return weekYear;
    }

    public OutGoingNotificationEntity setWeekYear(int weekYear) {
        this.weekYear = weekYear;
        return this;
    }

    public boolean isSent() {
        return sent;
    }

    public OutGoingNotificationEntity setSent(boolean sent) {
        this.sent = sent;
        return this;
    }
}
