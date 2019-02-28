package com.noqapp.domain.json.payment.cashfree;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2019-02-28 11:50
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonVerify extends AbstractDomain {

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("secretKey")
    private String secretKey;

    public String getAppId() {
        return appId;
    }

    public JsonVerify setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public JsonVerify setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }
}
