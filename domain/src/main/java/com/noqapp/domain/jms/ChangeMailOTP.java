package com.noqapp.domain.jms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * hitender
 * 7/21/20 12:44 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeMailOTP implements Serializable {
    @SuppressWarnings ({"unused"})
    @SerializedName("userId")
    private String userId;

    @SuppressWarnings ({"unused"})
    @SerializedName ("name")
    private String name;

    @SuppressWarnings ({"unused"})
    @SerializedName ("mailOTP")
    private String mailOTP;

    private ChangeMailOTP(String userId, String name, String mailOTP) {
        this.userId = userId;
        this.name = name;
        this.mailOTP = mailOTP;
    }

    public static ChangeMailOTP newInstance(String userId, String name, String mailOTP) {
        return new ChangeMailOTP(userId, name, mailOTP);
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getMailOTP() {
        return mailOTP;
    }

    @Override
    public String toString() {
        return "ChangeMailOTP{" +
            "userId='" + userId + '\'' +
            ", name='" + name + '\'' +
            ", mailOTP='" + mailOTP + '\'' +
            '}';
    }
}
