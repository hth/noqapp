package com.noqapp.domain.jms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * hitender
 * 7/21/20 3:20 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackMail implements Serializable {
    @SuppressWarnings ({"unused"})
    @SerializedName("userId")
    private String userId;

    @SuppressWarnings ({"unused"})
    @SerializedName ("qid")
    private String qid;

    @SuppressWarnings ({"unused"})
    @SerializedName ("name")
    private String name;

    @SuppressWarnings ({"unused"})
    @SerializedName ("subject")
    private String subject;

    @SuppressWarnings ({"unused"})
    @SerializedName ("body")
    private String body;

    private FeedbackMail(String userId, String qid, String name, String subject, String body) {
        this.userId = userId;
        this.qid = qid;
        this.name = name;
        this.subject = subject;
        this.body = body;
    }

    public static FeedbackMail newInstance(String userId, String qid, String name, String subject, String body) {
        return new FeedbackMail(userId, qid, name, subject, body);
    }

    public String getUserId() {
        return userId;
    }

    public String getQid() {
        return qid;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "FeedbackMail{" +
            "userId='" + userId + '\'' +
            ", qid='" + qid + '\'' +
            ", name='" + name + '\'' +
            ", subject='" + subject + '\'' +
            ", body='" + body + '\'' +
            '}';
    }
}
