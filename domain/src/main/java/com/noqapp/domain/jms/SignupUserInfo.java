package com.noqapp.domain.jms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * hitender
 * 7/21/20 2:49 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignupUserInfo implements Serializable {
    @SuppressWarnings ({"unused"})
    @SerializedName("userId")
    private String userId;

    @SuppressWarnings ({"unused"})
    @SerializedName ("qid")
    private String qid;

    @SuppressWarnings ({"unused"})
    @SerializedName ("name")
    private String name;

    private SignupUserInfo(String userId, String qid, String name) {
        this.userId = userId;
        this.qid = qid;
        this.name = name;
    }

    public static SignupUserInfo newInstance(String userId, String qid, String name) {
        return new SignupUserInfo(userId, qid, name);
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

    @Override
    public String toString() {
        return "SignupUserInfo{" +
            "userId='" + userId + '\'' +
            ", qid='" + qid + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
