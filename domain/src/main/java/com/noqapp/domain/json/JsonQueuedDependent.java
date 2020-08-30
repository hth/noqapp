package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.QueueUserStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.util.Base64;

/**
 * hitender
 * 3/7/18 6:05 PM
 */
@SuppressWarnings ({
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
public class JsonQueuedDependent extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueuedPerson.class);

    @JsonProperty("t")
    private int token;

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty ("n")
    private String customerName = "";

    @JsonProperty ("p")
    private String guardianPhone = "";

    @JsonProperty ("gid")
    private String guardianQueueUserId;

    @JsonProperty ("qu")
    private QueueUserStateEnum queueUserState;

    /* Format: 15+ days. */
    @JsonProperty ("age")
    private String age;

    @JsonProperty ("g")
    private GenderEnum gender;

    public int getToken() {
        return token;
    }

    public JsonQueuedDependent setToken(int token) {
        this.token = token;
        return this;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public JsonQueuedDependent setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonQueuedDependent setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonQueuedDependent setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public JsonQueuedDependent setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
        return this;
    }

    public String getGuardianQueueUserId() {
        return guardianQueueUserId;
    }

    public JsonQueuedDependent setGuardianQueueUserId(String guardianQueueUserId) {
        this.guardianQueueUserId = guardianQueueUserId;
        return this;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public JsonQueuedDependent setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
        return this;
    }

    public String getAge() {
        return age;
    }

    public JsonQueuedDependent setAge(String age) {
        this.age = age;
        return this;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public JsonQueuedDependent setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    @Transient
    public String getRecordReferenceId() {
        return Base64.getEncoder().encodeToString((token + "#" + guardianQueueUserId + "#" + queueUserId).getBytes());
    }
}
