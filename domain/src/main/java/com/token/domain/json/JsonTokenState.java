package com.token.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: hitender
 * Date: 12/1/16 9:28 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
public class JsonTokenState {
    private static final Logger LOG = LoggerFactory.getLogger(JsonTokenState.class);

    @JsonProperty ("c")
    private String code;

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("a")
    private String storeAddress;

    @JsonProperty ("p")
    private String storePhone;

    @JsonProperty ("s")
    private String servingNumber;

    @JsonProperty ("l")
    private String lastNumber;

    public JsonTokenState(String code) {
        this.code = code;
    }

    public JsonTokenState setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public JsonTokenState setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public JsonTokenState setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public JsonTokenState setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public JsonTokenState setServingNumber(String servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public JsonTokenState setLastNumber(String lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }
}
